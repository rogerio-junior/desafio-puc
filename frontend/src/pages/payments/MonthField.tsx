import type { ChangeEvent, MouseEvent } from 'react';
import Field from '../../components/Field';
import {
  formatMonthInput,
  isCompleteMonthInput,
  monthInputToPeriod,
  periodToMonthInput,
} from '../../utils/format';
import styles from './MonthField.module.css';

const MONTH_MASK = 'mm/aaaa';

// Nem todo navegador tem o seletor nativo de mês: nesses, fica só a digitação.
function supportsMonthPicker() {
  const input = document.createElement('input');
  input.type = 'month';
  return input.type === 'month';
}

const HAS_MONTH_PICKER = supportsMonthPicker();

type MonthFieldProps = {
  value: string;
  max: string;
  error: string;
  onChange: (value: string) => void;
};

function MonthField({ value, max, error, onChange }: MonthFieldProps) {
  function handlePickerChange(event: ChangeEvent<HTMLInputElement>) {
    onChange(event.target.value ? periodToMonthInput(event.target.value) : '');
  }

  // O Chrome só abre o seletor ao clicar exatamente no ícone; no resto da área
  // o clique apenas foca o campo. Abrindo pelo showPicker, qualquer ponto serve.
  function handlePickerClick(event: MouseEvent<HTMLInputElement>) {
    try {
      event.currentTarget.showPicker();
      event.preventDefault();
    } catch {
      // Navegador sem showPicker: segue o comportamento nativo.
    }
  }

  return (
    <Field
      className={styles.field}
      label="Mês"
      type="text"
      inputMode="numeric"
      style={{ paddingRight: 76 }}
      value={value}
      error={error}
      onChange={(event) => onChange(formatMonthInput(event.target.value))}
      onClear={() => onChange('')}
    >
      <span className={styles.mask}>
        <span className={styles.maskTyped}>{value}</span>
        {MONTH_MASK.slice(value.length)}
      </span>
      {HAS_MONTH_PICKER && (
        <input
          className={styles.picker}
          type="month"
          tabIndex={-1}
          title="Escolher mês"
          max={max}
          value={isCompleteMonthInput(value) ? monthInputToPeriod(value) : ''}
          onClick={handlePickerClick}
          onChange={handlePickerChange}
        />
      )}
    </Field>
  );
}

export default MonthField;
