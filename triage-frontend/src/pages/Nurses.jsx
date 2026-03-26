import { useState, useEffect } from 'react';
import { nursesApi } from '../api/client';
import Modal from '../components/Modal';
import './Page.css';

const EMPTY_FORM = {
  nurseId: '', firstName: '', lastName: '', phoneNumber: '',
  latitude: '', longitude: '', availability: 'AVAILABLE', yearsOfExperience: '',
};

export default function Nurses() {
  const [nurses, setNurses]       = useState([]);
  const [error, setError]         = useState('');
  const [modal, setModal]         = useState(null);   // null | 'add' | 'edit'
  const [form, setForm]           = useState(EMPTY_FORM);
  const [saving, setSaving]       = useState(false);

  useEffect(() => { loadNurses(); }, []);

  async function loadNurses() {
    try {
      const res = await nursesApi.getAll();
      setNurses(res.data.data || []);
    } catch {
      setError('Could not load nurses.');
    }
  }

  function openAdd() {
    setForm(EMPTY_FORM);
    setModal('add');
  }

  function openEdit(nurse) {
    setForm({
      nurseId:          nurse.nurseId,
      firstName:        nurse.firstName,
      lastName:         nurse.lastName,
      phoneNumber:      nurse.phoneNumber,
      latitude:         nurse.location?.latitude ?? '',
      longitude:        nurse.location?.longitude ?? '',
      availability:     nurse.availability ?? nurse.nurseAvailability ?? 'AVAILABLE',
      yearsOfExperience: nurse.yearsOfExperience,
    });
    setModal('edit');
  }

  function closeModal() { setModal(null); setError(''); }

  function onChange(e) {
    setForm(f => ({ ...f, [e.target.name]: e.target.value }));
  }

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    setError('');
    const payload = {
      ...form,
      latitude:          parseFloat(form.latitude),
      longitude:         parseFloat(form.longitude),
      yearsOfExperience: parseInt(form.yearsOfExperience, 10),
    };
    try {
      if (modal === 'add') {
        await nursesApi.add(payload);
      } else {
        await nursesApi.update(form.nurseId, payload);
      }
      closeModal();
      loadNurses();
    } catch (err) {
      setError(err.response?.data?.message || 'Save failed.');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(nurseId) {
    if (!window.confirm(`Delete nurse ${nurseId}?`)) return;
    try {
      await nursesApi.remove(nurseId);
      loadNurses();
    } catch {
      setError('Delete failed.');
    }
  }

  const available   = nurses.filter(n => (n.availability ?? n.nurseAvailability) === 'AVAILABLE');
  const unavailable = nurses.filter(n => (n.availability ?? n.nurseAvailability) !== 'AVAILABLE');

  return (
    <div className="page">
      <div className="page-header">
        <h1>Nurses</h1>
        <button className="btn-primary" onClick={openAdd}>+ Add Nurse</button>
      </div>

      {error && <div className="alert-error">{error}</div>}

      <div className="stats-row">
        <div className="stat-chip available">{available.length} Available</div>
        <div className="stat-chip unavailable">{unavailable.length} Unavailable</div>
      </div>

      <section className="card">
        <h2>Available Nurses</h2>
        <NurseTable nurses={available} onEdit={openEdit} onDelete={handleDelete} />
      </section>

      <section className="card">
        <h2>All Nurses</h2>
        <NurseTable nurses={nurses} onEdit={openEdit} onDelete={handleDelete} showBadge />
      </section>

      {modal && (
        <Modal title={modal === 'add' ? 'Add Nurse' : 'Edit Nurse'} onClose={closeModal}>
          <form onSubmit={handleSave} className="form-grid">
            <label>Nurse ID
              <input name="nurseId" value={form.nurseId} onChange={onChange}
                required disabled={modal === 'edit'} />
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
            <label>Latitude
              <input name="latitude" type="number" step="any" value={form.latitude} onChange={onChange} required />
            </label>
            <label>Longitude
              <input name="longitude" type="number" step="any" value={form.longitude} onChange={onChange} required />
            </label>
            <label>Years of Experience
              <input name="yearsOfExperience" type="number" min="0" value={form.yearsOfExperience} onChange={onChange} required />
            </label>
            <label>Availability
              <select name="availability" value={form.availability} onChange={onChange}>
                <option value="AVAILABLE">Available</option>
                <option value="UNAVAILABLE">Unavailable</option>
              </select>
            </label>
            {error && <div className="alert-error">{error}</div>}
            <div className="form-actions">
              <button type="button" className="btn-ghost" onClick={closeModal}>Cancel</button>
              <button type="submit" className="btn-primary" disabled={saving}>
                {saving ? 'Saving…' : 'Save'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}

function NurseTable({ nurses, onEdit, onDelete, showBadge }) {
  if (nurses.length === 0) return <p className="empty-msg">No nurses found.</p>;
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th><th>Name</th><th>Phone</th>
            <th>Experience</th><th>Location</th>
            {showBadge && <th>Status</th>}
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {nurses.map(n => {
            const avail = n.availability ?? n.nurseAvailability;
            return (
              <tr key={n.nurseId}>
                <td>{n.nurseId}</td>
                <td>{n.firstName} {n.lastName}</td>
                <td>{n.phoneNumber}</td>
                <td>{n.yearsOfExperience} yrs</td>
                <td>{n.location ? `${n.location.latitude.toFixed(3)}, ${n.location.longitude.toFixed(3)}` : '—'}</td>
                {showBadge && (
                  <td>
                    <span className={`badge ${avail === 'AVAILABLE' ? 'badge-green' : 'badge-red'}`}>
                      {avail}
                    </span>
                  </td>
                )}
                <td className="actions">
                  <button className="btn-sm" onClick={() => onEdit(n)}>Edit</button>
                  <button className="btn-sm danger" onClick={() => onDelete(n.nurseId)}>Delete</button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
