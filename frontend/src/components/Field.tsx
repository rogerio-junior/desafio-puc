import { useId, type ComponentProps } from 'react';
import styles from './Field.module.css';

type FieldProps = ComponentProps<'input'> & {
  label: string;
  error?: string;
  onClear?: () => void;
};

function Field({ label, error, onClear, className, ...inputProps }: FieldProps) {
  const id = useId();

  return (
    <div className={`${styles.field} ${className ?? ''}`}>
      <label htmlFor={id} className={styles.label}>
        {label}
      </label>
      <div className={styles.inputWrapper}>
        <input id={id} className={styles.input} {...inputProps} />
        {onClear && inputProps.value && (
          <button
            type="button"
            className={styles.clearButton}
            title={`Limpar ${label}`}
            onClick={onClear}
          >
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
              <path d="M3.5 3.5l9 9M12.5 3.5l-9 9" />
            </svg>
          </button>
        )}
      </div>
      {error && <p className={styles.error}>{error}</p>}
    </div>
  );
}

export default Field;
