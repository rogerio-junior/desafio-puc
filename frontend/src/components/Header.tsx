import { Link, NavLink } from 'react-router';
import styles from './Header.module.css';

const NAV_ITEMS = [
  { label: 'Fechamento', href: '/' },
  { label: 'Pagamentos', href: '/pagamentos' },
  { label: 'Funcionários', href: '/funcionarios' },
];

function Header() {
  return (
    <header className={styles.header}>
      <Link to="/" className={styles.brand}>
        <span className={styles.brandName}>
          Pagamento<span className={styles.brandDot}>.</span>Folha
        </span>
        <span className={styles.brandTagline}>DESAFIO PUC RIO</span>
      </Link>

      <nav className={styles.nav}>
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.href}
            to={item.href}
            end
            className={({ isActive }) =>
              `${styles.navItem} ${isActive ? styles.navItemActive : ''}`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </header>
  );
}

export default Header;
