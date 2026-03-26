import { NavLink } from 'react-router-dom';
import './Navbar.css';

export default function Navbar() {
  return (
    <nav className="navbar">
      <span className="navbar-brand">Triage System</span>
      <div className="navbar-links">
        <NavLink to="/"         end className={({ isActive }) => isActive ? 'active' : ''}>Dashboard</NavLink>
        <NavLink to="/nurses"       className={({ isActive }) => isActive ? 'active' : ''}>Nurses</NavLink>
        <NavLink to="/patients"     className={({ isActive }) => isActive ? 'active' : ''}>Patients</NavLink>
        <NavLink to="/symptoms"     className={({ isActive }) => isActive ? 'active' : ''}>Symptoms</NavLink>
      </div>
    </nav>
  );
}
