import type { ComponentProps, ReactNode } from 'react';
import styles from './Button.module.css';

type ButtonProps = ComponentProps<'button'> & {
  labels: Record<string, ReactNode>;
  activeLabel: string;
  variant?: 'primary' | 'outline';
};

// Todos os textos ocupam a mesma célula do grid, só o ativo fica visível.
// Assim o botão mantém a largura do maior texto ao trocar de estado.
function Button({ labels, activeLabel, variant = 'primary', ...buttonProps }: ButtonProps) {
  return (
    <button className={`${styles.button} ${styles[variant]}`} {...buttonProps}>
      <span className={styles.labels}>
        {Object.entries(labels).map(([key, label]) => (
          <span
            key={key}
            className={styles.label}
            style={{ visibility: key === activeLabel ? 'visible' : 'hidden' }}
          >
            {label}
          </span>
        ))}
      </span>
    </button>
  );
}

export default Button;
