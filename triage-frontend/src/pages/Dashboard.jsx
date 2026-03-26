import { useState, useEffect } from 'react';
import { assignmentsApi } from '../api/client';
import AssignmentBoard from '../components/AssignmentBoard';
import './Page.css';

export default function Dashboard() {
  const [latest, setLatest]   = useState(null);   // Map from latest run
  const [history, setHistory] = useState({});      // Full stored history
  const [running, setRunning] = useState(false);
  const [error, setError]     = useState('');
  const [showHistory, setShowHistory] = useState(false);

  useEffect(() => { loadHistory(); }, []);

  async function loadHistory() {
    try {
      const res = await assignmentsApi.getAll();
      setHistory(res.data.data || {});
    } catch {
      setError('Could not load assignment history.');
    }
  }

  async function handleRun() {
    setRunning(true);
    setError('');
    setLatest(null);
    try {
      const res = await assignmentsApi.run();
      if (res.data.success) {
        setLatest(res.data.data);
        await loadHistory();
      } else {
        setError(res.data.message);
      }
    } catch {
      setError('Failed to run assignment. Is the backend running?');
    } finally {
      setRunning(false);
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Dashboard</h1>
        <button className="btn-primary" onClick={handleRun} disabled={running}>
          {running ? 'Running…' : 'Run Assignment Algorithm'}
        </button>
      </div>

      {error && <div className="alert-error">{error}</div>}

      {latest && (
        <section className="card">
          <h2>Latest Run</h2>
          <AssignmentBoard assignments={latest} />
        </section>
      )}

      <section className="card">
        <div className="section-row">
          <h2>Assignment History</h2>
          <button className="btn-ghost" onClick={() => setShowHistory(v => !v)}>
            {showHistory ? 'Collapse' : 'Expand'}
          </button>
        </div>
        {showHistory && <AssignmentBoard assignments={history} />}
      </section>
    </div>
  );
}
