import { useState, useEffect } from 'react';
import { patientsApi, symptomsApi } from '../api/client';
import Modal from '../components/Modal';
import './Page.css';

const EMPTY_FORM = {
  patientId: '', firstName: '', lastName: '', phoneNumber: '',
  latitude: '', longitude: '', dateOfBirth: '',
};

const PRIORITY_ORDER = ['EMERGENCY','IMMEDIATE','URGENT','SEMIURGENT','NONURGENT'];

export default function Patients() {
  const [patients, setPatients]         = useState([]);
  const [allSymptoms, setAllSymptoms]   = useState([]);
  const [modal, setModal]               = useState(null); // null | 'add' | 'symptoms'
  const [form, setForm]                 = useState(EMPTY_FORM);
  const [selected, setSelected]         = useState(null); // patient for symptom modal
  const [checkedSymptoms, setChecked]   = useState([]);
  const [error, setError]               = useState('');
  const [saving, setSaving]             = useState(false);

  useEffect(() => {
    loadPatients();
    symptomsApi.getAll().then(r => setAllSymptoms(r.data.data || []));
  }, []);

  async function loadPatients() {
    try {
      const res = await patientsApi.getAll();
      setPatients(res.data.data || []);
    } catch {
      setError('Could not load patients.');
    }
  }

  function onChange(e) {
    setForm(f => ({ ...f, [e.target.name]: e.target.value }));
  }

  function openAdd() {
    setForm(EMPTY_FORM);
    setError('');
    setModal('add');
  }

  function openSymptoms(patient) {
    setSelected(patient);
    setChecked([]);
    setError('');
    setModal('symptoms');
  }

  function closeModal() { setModal(null); setSelected(null); setError(''); }

  async function handleAddPatient(e) {
    e.preventDefault();
    setSaving(true);
    setError('');
    const payload = {
      ...form,
      latitude:  parseFloat(form.latitude),
      longitude: parseFloat(form.longitude),
    };
    try {
      await patientsApi.add(payload);
      closeModal();
      loadPatients();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add patient.');
    } finally {
      setSaving(false);
    }
  }

  async function handleAssignSymptoms(e) {
    e.preventDefault();
    if (checkedSymptoms.length === 0) {
      setError('Select at least one symptom.');
      return;
    }
    setSaving(true);
    setError('');
    // API expects: [["symptom name", "PRIORITY_ENUM"], ...]
    const payload = checkedSymptoms.map(s => [s.name, s.priority]);
    try {
      await patientsApi.assignSymptoms(selected.patientId, payload);
      closeModal();
      loadPatients();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to assign symptoms.');
    } finally {
      setSaving(false);
    }
  }

  function toggleSymptom(symptom) {
    setChecked(prev =>
      prev.find(s => s.name === symptom.name)
        ? prev.filter(s => s.name !== symptom.name)
        : [...prev, symptom]
    );
  }

  // Group symptoms by priority for the checklist
  const groupedSymptoms = PRIORITY_ORDER.reduce((acc, p) => {
    const group = allSymptoms.filter(s => s.symptomPriority === p || s.priority === p);
    if (group.length) acc[p] = group;
    return acc;
  }, {});

  return (
    <div className="page">
      <div className="page-header">
        <h1>Patients</h1>
        <button className="btn-primary" onClick={openAdd}>+ Add Patient</button>
      </div>

      {error && <div className="alert-error">{error}</div>}

      <section className="card">
        <div className="table-wrap">
          {patients.length === 0
            ? <p className="empty-msg">No patients found.</p>
            : (
              <table>
                <thead>
                  <tr>
                    <th>ID</th><th>Name</th><th>DOB</th>
                    <th>Phone</th><th>Triage Score</th><th>Symptoms</th><th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {patients.map(p => (
                    <tr key={p.patientId}>
                      <td>{p.patientId}</td>
                      <td>{p.firstName} {p.lastName}</td>
                      <td>{p.dateOfBirth}</td>
                      <td>{p.phoneNumber}</td>
                      <td>
                        <span className={`badge ${scoreColor(p.triageScore)}`}>
                          {p.triageScore || '—'}
                        </span>
                      </td>
                      <td>
                        {p.medicalSymptoms?.length
                          ? p.medicalSymptoms.map(s => s.name).join(', ')
                          : <span className="muted">None</span>}
                      </td>
                      <td className="actions">
                        <button className="btn-sm" onClick={() => openSymptoms(p)}>
                          Assign Symptoms
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )
          }
        </div>
      </section>

      {/* Add Patient Modal */}
      {modal === 'add' && (
        <Modal title="Add Patient" onClose={closeModal}>
          <form onSubmit={handleAddPatient} className="form-grid">
            <label>Patient ID
              <input name="patientId" value={form.patientId} onChange={onChange} required />
            </label>
            <label>First Name
              <input name="firstName" value={form.firstName} onChange={onChange} required />
            </label>
            <label>Last Name
              <input name="lastName" value={form.lastName} onChange={onChange} required />
            </label>
            <label>Phone Number
              <input name="phoneNumber" value={form.phoneNumber} onChange={onChange} required />
            </label>
            <label>Date of Birth
              <input name="dateOfBirth" type="date" value={form.dateOfBirth} onChange={onChange} required />
            </label>
            <label>Latitude
              <input name="latitude" type="number" step="any" value={form.latitude} onChange={onChange} required />
            </label>
            <label>Longitude
              <input name="longitude" type="number" step="any" value={form.longitude} onChange={onChange} required />
            </label>
            {error && <div className="alert-error">{error}</div>}
            <div className="form-actions">
              <button type="button" className="btn-ghost" onClick={closeModal}>Cancel</button>
              <button type="submit" className="btn-primary" disabled={saving}>
                {saving ? 'Saving…' : 'Add Patient'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Assign Symptoms Modal */}
      {modal === 'symptoms' && selected && (
        <Modal title={`Assign Symptoms — ${selected.firstName} ${selected.lastName}`} onClose={closeModal}>
          <form onSubmit={handleAssignSymptoms}>
            {Object.keys(groupedSymptoms).length === 0
              ? <p className="empty-msg">No symptoms in database yet.</p>
              : PRIORITY_ORDER.filter(p => groupedSymptoms[p]).map(priority => (
                <div key={priority} className="symptom-group">
                  <div className={`symptom-group-label priority-${priority.toLowerCase()}`}>
                    {priority}
                  </div>
                  {groupedSymptoms[priority].map(s => (
                    <label key={s.name} className="symptom-check">
                      <input
                        type="checkbox"
                        checked={!!checkedSymptoms.find(c => c.name === s.name)}
                        onChange={() => toggleSymptom({ name: s.name, priority })}
                      />
                      {s.name}
                    </label>
                  ))}
                </div>
              ))
            }
            {error && <div className="alert-error">{error}</div>}
            <div className="form-actions" style={{ marginTop: '1rem' }}>
              <button type="button" className="btn-ghost" onClick={closeModal}>Cancel</button>
              <button type="submit" className="btn-primary" disabled={saving}>
                {saving ? 'Saving…' : 'Assign'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}

function scoreColor(score) {
  if (score >= 5) return 'badge-red';
  if (score >= 4) return 'badge-orange';
  if (score >= 3) return 'badge-yellow';
  if (score >= 1) return 'badge-green';
  return 'badge-grey';
}
