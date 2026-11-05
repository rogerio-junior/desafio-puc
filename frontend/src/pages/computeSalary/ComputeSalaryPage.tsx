import { useEffect, useState, type ChangeEvent, type SubmitEvent } from 'react';
import { useLocation, useSearchParams } from 'react-router';
import { ApiError } from '../../api/client/ApiError';
import { computeSalary } from '../../api/client/payments';
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
  isValidDate,
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

function readReferenceDate(params: URLSearchParams, today: string) {
  const date = params.get('referenceDate') ?? '';
  return isValidDate(date) ? date : today;
}

function ComputeSalaryPage() {
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const [today] = useState(getToday);
  const [referenceDate, setReferenceDate] = useState(() => readReferenceDate(searchParams, today));
  const [validationError, setValidationError] = useState('');
  const [seenLocationKey, setSeenLocationKey] = useState(location.key);
  const { state, call, reset } = useApiCall(computeSalary);

  // Mudanças na URL que não vieram do campo de data, como o menu ou o botão voltar.
  const isExternalNavigation = !location.state?.fromDateInput;
  if (seenLocationKey !== location.key) {
    setSeenLocationKey(location.key);
    if (isExternalNavigation) {
      setReferenceDate(readReferenceDate(searchParams, today));
      setValidationError('');
    }
  }

  useEffect(() => {
    if (isExternalNavigation) reset();
  }, [location.key, isExternalNavigation, reset]);

  const isDateRejected =
    state.isFailed &&
    state.error instanceof ApiError &&
    state.error.status === 400;
  const isUnavailable = state.isFailed && !isDateRejected;
  const period = getPayrollPeriod(referenceDate || today);
  const periodRange = formatPeriodRange(period);

  function handleDateChange(event: ChangeEvent<HTMLInputElement>) {
    const date = event.target.value;

    setReferenceDate(date);
    setValidationError('');
    reset();
    setSearchParams(date ? { referenceDate: date } : {}, {
      replace: true,
      state: { fromDateInput: true },
    });
  }

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
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
