import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar      from './components/Navbar';
import Dashboard   from './pages/Dashboard';
import Nurses      from './pages/Nurses';
import Patients    from './pages/Patients';
import Symptoms    from './pages/Symptoms';
import './App.css';

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/"         element={<Dashboard />} />
        <Route path="/nurses"   element={<Nurses />} />
        <Route path="/patients" element={<Patients />} />
        <Route path="/symptoms" element={<Symptoms />} />
      </Routes>
    </BrowserRouter>
  );
}
