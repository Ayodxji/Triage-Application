import { useState, useEffect } from 'react';
import { symptomsApi } from '../api/client';
import './Page.css';

const PRIORITY_META = {
  EMERGENCY:  { label: 'Emergency',   color: 'badge-red' },
  IMMEDIATE:  { label: 'Immediate',   color: 'badge-orange' },
  URGENT:     { label: 'Urgent',      color: 'badge-yellow' },
  SEMIURGENT: { label: 'Semi-Urgent', color: 'badge-blue' },
  NONURGENT:  { label: 'Non-Urgent',  color: 'badge-green' },
};

export default function Symptoms() {
  const [symptoms, setSymptoms] = useState([]);
  const [error, setError]       = useState('');

  useEffect(() => {
    symptomsApi.getAll()
      .then(r => setSymptoms(r.data.data || []))
      .catch(() => setError('Could not load symptoms.'));
  }, []);

  return (
    <div className="page">
      <div className="page-header">
        <h1>Symptoms Reference</h1>
      </div>

      {error && <div className="alert-error">{error}</div>}

      <section className="card">
        <p className="muted" style={{ marginBottom: '1rem' }}>
          These symptoms are pre-defined in the database. Assign them to patients from the Patients page.
        </p>
        <div className="table-wrap">
          {symptoms.length === 0
            ? <p className="empty-msg">No symptoms found. Populate the symptoms table in your database first.</p>
            : (
              <table>
                <thead>
                  <tr><th>Symptom</th><th>Priority</th><th>Urgency Value</th></tr>
                </thead>
                <tbody>
                  {symptoms.map((s, i) => {
                    const priority = s.symptomPriority ?? s.priority;
                    const meta = PRIORITY_META[priority] || { label: priority, color: 'badge-grey' };
                    return (
                      <tr key={i}>
                        <td>{s.name}</td>
                        <td><span className={`badge ${meta.color}`}>{meta.label}</span></td>
                        <td>{urgencyValue(priority)}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )
          }
        </div>
      </section>
    </div>
  );
}

function urgencyValue(priority) {
  return { EMERGENCY: 5, IMMEDIATE: 4, URGENT: 3, SEMIURGENT: 2, NONURGENT: 1 }[priority] ?? '—';
}
