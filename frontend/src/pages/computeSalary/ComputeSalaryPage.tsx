import { useState, type ChangeEvent, type FormEvent } from 'react';
import { ApiError } from '../../api/ApiError';
import { computeSalary } from '../../api/payments';
import Button from '../../components/Button';
import Notice from '../../components/Notice';
import Field from '../../components/Field';
import Spinner from '../../components/Spinner';
import { useApiCall } from '../../hooks/useApiCall';
import {
  formatPeriodRange,
  getMonthName,
  getPayrollPeriod,
  getToday,
} from '../../utils/payrollPeriod';
import SalaryBonusRules from './SalaryBonusRules';
import SalaryResult from './SalaryResult';
import styles from './ComputeSalaryPage.module.css';

const BUTTON_LABELS = {
  initial: 'Calcular folha',
  success: 'Calcular folha',
  failed: 'Tentar novamente',
  loading: (
    <>
      Calculando
      <Spinner />
    </>
  ),
};

function ComputeSalaryPage() {
  const [today] = useState(getToday);
  const [referenceDate, setReferenceDate] = useState(today);
  const [validationError, setValidationError] = useState('');
  const { state, call, reset } = useApiCall(computeSalary);

  const isDateRejected =
    state.isFailed &&
    state.error instanceof ApiError &&
    state.error.status === 400;
  const isUnavailable = state.isFailed && !isDateRejected;
  const period = getPayrollPeriod(referenceDate || today);
  const periodRange = formatPeriodRange(period);

  function handleDateChange(event: ChangeEvent<HTMLInputElement>) {
    setReferenceDate(event.target.value);
    setValidationError('');
    reset();
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!referenceDate) {
      setValidationError('Informe a data de referência.');
      return;
    }
    if (referenceDate > today) {
      setValidationError('A data de referência não pode estar no futuro.');
      return;
    }

    setValidationError('');
    call(referenceDate);
  }

  let note = `Será calculado o período de ${periodRange}, mês anterior à data de referência.`;
  if (state.isLoading) {
    note = `Aguarde, estamos calculando os pagamentos do período de ${periodRange}…`;
  } else if (state.isSuccess) {
    note = `Calculado o período de ${periodRange}, mês anterior à data de referência.`;
  }

  return (
    <main className={`rise ${styles.page}`}>
      <h1 className={styles.title}>
        Fechamento de <em className={styles.month}>{getMonthName(period)}</em>
      </h1>

      <div className={styles.columns}>
        <section>
          <form className={styles.form} onSubmit={handleSubmit} noValidate>
            <Field
              className={styles.dateField}
              label="Data de referência"
              type="date"
              max={today}
              value={referenceDate}
              onChange={handleDateChange}
              disabled={state.isLoading}
              error={
                isDateRejected
                  ? 'A API recusou esta data. Confira e tente de novo.'
                  : validationError
              }
            />
            <Button
              type="submit"
              disabled={state.isLoading}
              labels={BUTTON_LABELS}
              activeLabel={isDateRejected ? 'initial' : state.status}
            />
          </form>

          <p className={styles.note}>{note}</p>

          {isUnavailable && (
            <Notice
              type="error"
              title="Não foi possível calcular a folha"
              message="O sistema está indisponível"
              description="Tente novamente em alguns minutos. Quem já tem pagamento no mês não é recalculado."
            />
          )}

          {state.isSuccess && <SalaryResult result={state.data} />}
        </section>

        <SalaryBonusRules />
      </div>
    </main>
  );
}

export default ComputeSalaryPage;
