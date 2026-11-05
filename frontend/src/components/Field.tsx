import { useId, type ComponentProps } from 'react';
import styles from './Field.module.css';

type FieldProps = ComponentProps<'input'> & {
  label: string;
  error?: string;
};

function Field({ label, error, className, ...inputProps }: FieldProps) {
  const id = useId();

  return (
    <div className={`${styles.field} ${className ?? ''}`}>
      <label htmlFor={id} className={styles.label}>
        {label}
      </label>
      <input id={id} className={styles.input} {...inputProps} />
      {error && <p className={styles.error}>{error}</p>}
    </div>
  );
}

export default Field;
