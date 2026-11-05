import Header from './components/Header'
import ClosingPage from './pages/ClosingPage'
import styles from './App.module.css'

function App() {
  return (
    <div className={styles.app}>
      <div className={styles.container}>
        <Header current="Fechamento" />
        <ClosingPage />
      </div>
    </div>
  )
}

export default App
