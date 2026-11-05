import styles from './Header.module.css'

const NAV_ITEMS = [
  { label: 'Fechamento', href: '/' },
  { label: 'Pagamentos', href: '/pagamentos' },
  { label: 'Funcionários', href: '/funcionarios' },
]

type HeaderProps = {
  current: string
}

function Header({ current }: HeaderProps) {
  return (
    <header className={styles.header}>
      <a
        href="/"
        className={styles.brand}
        aria-label="Pagamento.Folha, Desafio PUC Rio, ir para Fechamento"
      >
        <span className={styles.brandName}>
          Pagamento<span className={styles.brandDot}>.</span>Folha
        </span>
        <span className={styles.brandTagline}>DESAFIO PUC RIO</span>
      </a>

      <nav className={styles.nav} aria-label="Principal">
        {NAV_ITEMS.map((item) => (
          <a
            key={item.href}
            href={item.href}
            className={styles.navItem}
            aria-current={item.label === current ? 'page' : undefined}
          >
            {item.label}
          </a>
        ))}
      </nav>
    </header>
  )
}

export default Header
