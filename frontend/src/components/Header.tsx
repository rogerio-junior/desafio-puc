import styles from './Header.module.css';

const NAV_ITEMS = [
  { label: 'Fechamento', href: '/' },
  { label: 'Pagamentos', href: '/pagamentos' },
  { label: 'Funcionários', href: '/funcionarios' },
];

type HeaderProps = {
  current: string;
};

function Header({ current }: HeaderProps) {
  return (
    <header className={styles.header}>
      <a href="/" className={styles.brand}>
        <span className={styles.brandName}>
          Pagamento<span className={styles.brandDot}>.</span>Folha
        </span>
        <span className={styles.brandTagline}>DESAFIO PUC RIO</span>
      </a>

      <nav className={styles.nav}>
        {NAV_ITEMS.map((item) => (
          <a
            key={item.href}
            href={item.href}
            className={`${styles.navItem} ${item.label === current ? styles.navItemActive : ''}`}
          >
            {item.label}
          </a>
        ))}
      </nav>
    </header>
  );
}

export default Header;
