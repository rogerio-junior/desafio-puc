import { useEffect, useState, type ChangeEvent, type SubmitEvent } from 'react';
import { useLocation, useSearchParams } from 'react-router';
import { ApiError } from '../../api/client/ApiError';
import {
  fetchPaymentsCsv,
  findEmployeePayments,
  findPaymentsByMonth,
} from '../../api/client/payments';
import Button from '../../components/Button';
import Field from '../../components/Field';
import Notice from '../../components/Notice';
import Pagination from '../../components/Pagination';
import Skeleton from '../../components/Skeleton';
import { useApiCall } from '../../hooks/useApiCall';
import { downloadFile } from '../../utils/downloadFile';
import {
  formatCpf,
  isCompleteMonthInput,
  monthInputToPeriod,
  periodToMonthInput,
} from '../../utils/format';
import {
  getLastPageOffset,
  readLimit,
  readOffset,
  setPaginationParams,
} from '../../utils/pagination';
import { getMonthName, getPayrollPeriod, getToday, isValidPeriod } from '../../utils/payrollPeriod';
import ExportButton from './ExportButton';
import MonthField from './MonthField';
import PaymentHistory from './PaymentHistory';
import PaymentTable from './PaymentTable';
import styles from './PaymentsPage.module.css';

const TABLE_PAGE_SIZE = 10;
const HISTORY_PAGE_SIZE = 12;
const SKELETON_ROWS = [1, 2, 3, 4, 5, 6];

const BUTTON_LABELS = {
  initial: 'Buscar',
  success: 'Buscar',
  loading: 'Buscando…',
  failed: 'Tentar novamente',
};

type PaymentQuery = {
  month: string;
  cpf: string;
};

function getPageSize(query: PaymentQuery) {
  return query.cpf ? HISTORY_PAGE_SIZE : TABLE_PAGE_SIZE;
}

function getCsvFileName(month: string) {
  return `pagamentos-${month}.csv`;
}

function formatMonthWords(period: string) {
  return `${getMonthName(period)} de ${period.slice(0, 4)}`;
}

function readQueryFromUrl(params: URLSearchParams): PaymentQuery {
  const month = params.get('month') ?? '';
  const cpf = params.get('cpf') ?? '';

  return {
    month: isValidPeriod(month) ? month : '',
    cpf: /^\d{11}$/.test(cpf) ? cpf : '',
  };
}

function toSearchParams({ month, cpf }: PaymentQuery, limit: number | null, offset: number) {
  const params = new URLSearchParams();
  if (month) params.set('month', month);
  if (cpf) params.set('cpf', cpf);
  setPaginationParams(params, limit, offset);
  return params;
}

// Os dois endpoints têm formatos diferentes: type indica qual deles respondeu.
async function searchPayments({ month, cpf }: PaymentQuery, limit: number, offset: number) {
  if (cpf) {
    const { employee, paymentsPage } = await findEmployeePayments(cpf, month, limit, offset);
    return { type: 'history', employee, page: paymentsPage } as const;
  }

  const page = await findPaymentsByMonth(month, limit, offset);
  return { type: 'month', page } as const;
}

async function exportPaymentsCsv(month: string) {
  const csv = await fetchPaymentsCsv(month);
  downloadFile(getCsvFileName(month), csv, 'text/csv;charset=utf-8');
}

function PaymentsPage() {
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const { month: urlMonth, cpf: urlCpf } = readQueryFromUrl(searchParams);
  const urlLimit = readLimit(searchParams);
  const limit = urlLimit ?? getPageSize({ month: urlMonth, cpf: urlCpf });
  const offset = readOffset(searchParams);
  const [today] = useState(getToday);

  const [month, setMonth] = useState(urlMonth ? periodToMonthInput(urlMonth) : '');
  const [cpf, setCpf] = useState(formatCpf(urlCpf));
  const [monthError, setMonthError] = useState('');
  const [cpfError, setCpfError] = useState('');
  const [hasChangedFilters, setHasChangedFilters] = useState(false);
  const [seenLocationKey, setSeenLocationKey] = useState(location.key);

  const { state, call, reset } = useApiCall(searchPayments);
  const {
    state: exportState,
    call: callExport,
    reset: resetExport,
  } = useApiCall(exportPaymentsCsv);

  // A URL é a origem da busca: toda navegação atualiza os campos e busca de novo.
  if (seenLocationKey !== location.key) {
    setSeenLocationKey(location.key);
    setMonth(urlMonth ? periodToMonthInput(urlMonth) : '');
    setCpf(formatCpf(urlCpf));
    setMonthError('');
    setCpfError('');
    setHasChangedFilters(false);
  }

  useEffect(() => {
    resetExport();

    if (urlMonth || urlCpf) {
      call({ month: urlMonth, cpf: urlCpf }, limit, offset);
    } else {
      reset();
    }
  }, [location.key, urlMonth, urlCpf, limit, offset, call, reset, resetExport]);

  // Enquanto os campos estão sendo editados, não há busca aplicada na tela.
  const query = !hasChangedFilters && (urlMonth || urlCpf) ? { month: urlMonth, cpf: urlCpf } : null;

  // Um offset além do fim da lista, digitado na URL, leva para a última página.
  const isPastLastPage = state.isSuccess && state.data.page.items.length === 0 && state.data.page.total > 0;
  const lastPageOffset = state.isSuccess ? getLastPageOffset(state.data.page.total, limit) : 0;

  useEffect(() => {
    if (isPastLastPage) {
      const urlQuery = { month: urlMonth, cpf: urlCpf };
      setSearchParams(toSearchParams(urlQuery, urlLimit, lastPageOffset), { replace: true });
    }
  }, [isPastLastPage, lastPageOffset, urlMonth, urlCpf, urlLimit, setSearchParams]);

  function changePage(nextOffset: number) {
    setSearchParams(toSearchParams({ month: urlMonth, cpf: urlCpf }, urlLimit, nextOffset));
  }

  // Qualquer mudança nos filtros limpa a tela: o resultado só aparece depois de buscar.
  function clearResults() {
    setMonthError('');
    setCpfError('');
    setHasChangedFilters(true);
    reset();
    resetExport();
  }

  function handleMonthChange(value: string) {
    setMonth(value);
    clearResults();
  }

  function handleCpfChange(event: ChangeEvent<HTMLInputElement>) {
    setCpf(formatCpf(event.target.value));
    clearResults();
  }

  function handleCpfClear() {
    setCpf('');
    clearResults();
  }

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();

    const cpfDigits = cpf.replace(/\D/g, '');
    const isMonthInvalid = month !== '' && !isCompleteMonthInput(month);
    const isCpfInvalid = cpfDigits !== '' && cpfDigits.length !== 11;

    if (!month && !cpfDigits) {
      setMonthError('Informe o mês ou o CPF para buscar.');
      return;
    }
    if (isMonthInvalid || isCpfInvalid) {
      setMonthError(isMonthInvalid ? 'Informe o mês completo, no formato mm/aaaa.' : '');
      setCpfError(isCpfInvalid ? 'O CPF precisa ter 11 dígitos.' : '');
      return;
    }

    setMonthError('');
    setCpfError('');

    // Ao tentar de novo depois de uma falha, continua na mesma página da lista.
    if (state.isFailed && query) {
      resetExport();
      call(query, limit, offset);
      return;
    }

    setHasChangedFilters(false);
    setSearchParams(toSearchParams(
      { month: month ? monthInputToPeriod(month) : '', cpf: cpfDigits },
      urlLimit,
      0,
    ));
  }

  const isByCpf = Boolean(query?.cpf);
  const isByMonth = query !== null && !query.cpf;
  const isEmpty = state.isSuccess && state.data.page.total === 0;
  const isEmployeeNotFound =
    state.isFailed &&
    state.error instanceof ApiError &&
    state.error.status === 404;
  const isUnavailable = state.isFailed && !isEmployeeNotFound;
  const employee = state.isSuccess && state.data.type === 'history' ? state.data.employee : null;

  let titleDetail = '';
  if (isByMonth) {
    const words = formatMonthWords(query.month);
    titleDetail = `de ${words.charAt(0).toUpperCase()}${words.slice(1)}`;
  } else if (isByCpf && !state.isLoading) {
    titleDetail = employee ? `de ${employee.firstName}` : 'do CPF';
  }

  let exportDisabledReason = '';
  if (!query) exportDisabledReason = 'Busque um mês para exportar.';
  else if (isByCpf) exportDisabledReason = 'A exportação vale só para a busca por mês, sem CPF.';
  else if (state.isLoading) exportDisabledReason = 'Aguarde a busca terminar.';
  else if (state.isFailed) exportDisabledReason = 'A busca falhou. Tente de novo para exportar.';
  else if (isEmpty) exportDisabledReason = 'Não há pagamentos neste mês para exportar.';

  let emptyMessage = '';
  let emptyDescription = '';
  if (query && isEmployeeNotFound) {
    emptyMessage = `Nenhum funcionário com o CPF ${formatCpf(query.cpf)}`;
    emptyDescription = 'Confira se os números estão certos e busque de novo.';
  } else if (query && isEmpty) {
    const cpfLabel = formatCpf(query.cpf);
    const employeeName = `${employee?.firstName} ${employee?.lastName}`;
    if (query.cpf && query.month) {
      emptyMessage = `Nenhum pagamento para o CPF ${cpfLabel} em ${formatMonthWords(query.month)}`;
      emptyDescription = `Limpe o mês para ver todos os pagamentos de ${employeeName}.`;
    } else if (query.cpf) {
      emptyMessage = `Nenhum pagamento para o CPF ${cpfLabel}`;
      emptyDescription = `Há um cadastro de ${employeeName}, mas ainda sem pagamentos registrados.`;
    } else {
      emptyMessage = `Nenhum pagamento em ${formatMonthWords(query.month)}`;
      emptyDescription = 'Se esperava pagamentos neste mês, confira se a folha já foi calculada em Fechamento.';
    }
  }

  return (
    <main className={`rise ${styles.page}`}>
      <h1 className={styles.title}>
        Pagamentos <em className={styles.titleDetail}>{titleDetail}</em>
      </h1>

      <form className={styles.form} onSubmit={handleSubmit} noValidate>
        <MonthField
          value={month}
          max={getPayrollPeriod(today)}
          error={monthError}
          onChange={handleMonthChange}
        />
        <Field
          className={styles.cpfField}
          label="CPF"
          type="text"
          inputMode="numeric"
          placeholder="000.000.000-00"
          value={cpf}
          error={cpfError}
          onChange={handleCpfChange}
          onClear={handleCpfClear}
        />
        <Button
          type="submit"
          disabled={state.isLoading}
          labels={BUTTON_LABELS}
          activeLabel={isEmployeeNotFound ? 'initial' : state.status}
        />
        <ExportButton
          isLoading={exportState.isLoading}
          disabledReason={exportDisabledReason}
          onClick={() => {
            if (query) callExport(query.month);
          }}
        />
      </form>

      {exportState.isFailed && (
        <Notice
          type="error"
          title="Não foi possível exportar o CSV"
          message="O sistema está indisponível"
          description="Tente novamente em alguns minutos."
        />
      )}

      {state.isLoading && (
        <div className={styles.skeletonList}>
          {SKELETON_ROWS.map((row) => (
            <div key={row} className={styles.skeletonRow}>
              <Skeleton width="58%" />
              <Skeleton width="70%" />
              <span className={styles.skeletonSalary}>
                <Skeleton width="64px" />
              </span>
            </div>
          ))}
        </div>
      )}

      {isUnavailable && (
        <Notice
          type="error"
          title="Não foi possível carregar os pagamentos"
          message="O sistema está indisponível"
          description="Tente novamente em alguns minutos."
        />
      )}

      {(isEmpty || isEmployeeNotFound) && (
        <Notice
          type="info"
          title="Nada encontrado"
          message={emptyMessage}
          description={emptyDescription}
        />
      )}

      {state.isSuccess && !isEmpty && query && (
        <>
          {state.data.type === 'history' ? (
            <PaymentHistory employee={state.data.employee} payments={state.data.page.items} />
          ) : (
            <PaymentTable payments={state.data.page.items} />
          )}
          <Pagination
            count={`${state.data.page.total} ${state.data.page.total === 1 ? 'pagamento' : 'pagamentos'}`}
            offset={offset}
            pageSize={limit}
            total={state.data.page.total}
            onChange={changePage}
          />
        </>
      )}
    </main>
  );
}

export default PaymentsPage;
