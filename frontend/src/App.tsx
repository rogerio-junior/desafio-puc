import { BrowserRouter, Navigate, Route, Routes } from 'react-router';
import Header from './components/Header';
import ComputeSalaryPage from './pages/computeSalary/ComputeSalaryPage';
import EmployeesPage from './pages/employees/EmployeesPage';
import styles from './App.module.css';

function App() {
  return (
    <BrowserRouter>
      <div className={styles.app}>
        <div className={styles.container}>
          <Header />
          <Routes>
            <Route path="/" element={<ComputeSalaryPage />} />
            <Route path="/funcionarios" element={<EmployeesPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;
