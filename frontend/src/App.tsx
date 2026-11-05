import Header from './components/Header'
import ComputeSalaryPage from './pages/ComputeSalaryPage'
import styles from './App.module.css'

function App() {
  return (
    <div className={styles.app}>
      <div className={styles.container}>
        <Header current="Fechamento" />
        <ComputeSalaryPage />
      </div>
    </div>
  )
}

export default App
