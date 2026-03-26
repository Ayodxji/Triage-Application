import './AssignmentBoard.css';

export default function AssignmentBoard({ assignments, title }) {
  const entries = Object.entries(assignments);

  if (entries.length === 0) {
    return <p className="empty-msg">No assignments found.</p>;
  }

  return (
    <div>
      {title && <h3 className="board-title">{title}</h3>}
      <div className="board-grid">
        {entries.map(([nurseId, patients]) => (
          <div key={nurseId} className="nurse-card">
            <div className="nurse-card-header">
              <span className="nurse-id">{nurseId}</span>
              <span className="patient-count">{patients.length} patient{patients.length !== 1 ? 's' : ''}</span>
            </div>
            <ul className="patient-list">
              {patients.map((pid) => (
                <li key={pid}>{pid}</li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    </div>
  );
}
