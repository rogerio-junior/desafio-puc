import { useEffect, useState, type ChangeEvent, type SubmitEvent } from 'react';
import { useLocation, useSearchParams } from 'react-router';
import { findActiveEmployeesByCep } from '../../api/client/employees';
import Button from '../../components/Button';
import Field from '../../components/Field';
import Notice from '../../components/Notice';
import Pagination from '../../components/Pagination';
import RequestErrorNotice from '../../components/RequestErrorNotice';
import Skeleton from '../../components/Skeleton';
import { useApiCall } from '../../hooks/useApiCall';
import { formatCep } from '../../utils/format';
import {
  getLastPageOffset,
  readLimit,
  readOffset,
  setPaginationParams,
} from '../../utils/pagination';
import EmployeeTable from './EmployeeTable';
import styles from './EmployeesPage.module.css';

const PAGE_SIZE = 10;
const SKELETON_ROWS = [1, 2, 3];

const BUTTON_LABELS = {
  initial: 'Buscar',
  success: 'Buscar',
  loading: 'Buscando…',
  failed: 'Tentar novamente',
};

const CEP_PATTERN = /^\d{5}-\d{3}$/;

// Na URL o CEP fica só com dígitos; no campo e na API, no formato 00000-000.
function readCep(params: URLSearchParams) {
  const cep = params.get('cep') ?? '';
  return /^\d{8}$/.test(cep) ? formatCep(cep) : '';
}

function toSearchParams(cep: string, limit: number | null, offset: number) {
  const params = new URLSearchParams();
  if (cep) params.set('cep', cep.replace('-', ''));
  setPaginationParams(params, limit, offset);
  return params;
}

function EmployeesPage() {
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const urlCep = readCep(searchParams);
  const urlLimit = readLimit(searchParams);
  const limit = urlLimit ?? PAGE_SIZE;
  const offset = readOffset(searchParams);

  const [cep, setCep] = useState(urlCep);
  const [validationError, setValidationError] = useState('');
  const [seenLocationKey, setSeenLocationKey] = useState(location.key);
  const { state, call, reset } = useApiCall(findActiveEmployeesByCep);

  // A URL é a origem da busca: toda navegação atualiza o campo e busca de novo.
  if (seenLocationKey !== location.key) {
    setSeenLocationKey(location.key);
    setCep(urlCep);
    setValidationError('');
  }

  useEffect(() => {
    call(urlCep, limit, offset);
  }, [location.key, urlCep, limit, offset, call]);

  // Um offset além do fim da lista, digitado na URL, leva para a última página.
  const isPastLastPage = state.isSuccess && state.data.items.length === 0 && state.data.total > 0;
  const lastPageOffset = state.isSuccess ? getLastPageOffset(state.data.total, limit) : 0;

  useEffect(() => {
    if (isPastLastPage) {
      setSearchParams(toSearchParams(urlCep, urlLimit, lastPageOffset), { replace: true });
    }
  }, [isPastLastPage, lastPageOffset, urlCep, urlLimit, setSearchParams]);

  function changePage(nextOffset: number) {
    setSearchParams(toSearchParams(urlCep, urlLimit, nextOffset));
  }

  function handleCepChange(event: ChangeEvent<HTMLInputElement>) {
    setCep(formatCep(event.target.value));
    setValidationError('');
    if (state.isFailed) reset();
  }

  function handleClear() {
    setCep('');
    setValidationError('');
    if (urlCep) setSearchParams(toSearchParams('', urlLimit, 0));
  }

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();

    if (cep && !CEP_PATTERN.test(cep)) {
      setValidationError('Use o formato 00000-000.');
      return;
    }

    setValidationError('');

    // Ao tentar de novo depois de uma falha, continua na mesma página da lista.
    if (state.isFailed) {
      call(urlCep, limit, offset);
      return;
    }

    setSearchParams(toSearchParams(cep, urlLimit, 0));
  }

  return (
    <main className={`rise ${styles.page}`}>
      <h1 className={styles.title}>
        Funcionários <em className={styles.titleDetail}>ativos</em>
      </h1>

      <form className={styles.form} onSubmit={handleSubmit} noValidate>
        <Field
          className={styles.cepField}
          label="CEP"
          type="text"
          inputMode="numeric"
          placeholder="00000-000"
          value={cep}
          onChange={handleCepChange}
          onClear={handleClear}
          error={validationError}
        />
        <Button
          type="submit"
          disabled={state.isLoading}
          labels={BUTTON_LABELS}
          activeLabel={state.status}
        />
      </form>

      {state.isLoading && (
        <div className={styles.skeletonList}>
          {SKELETON_ROWS.map((row) => (
            <div key={row} className={styles.skeletonRow}>
              <Skeleton width="42%" />
              <Skeleton width="76%" />
            </div>
          ))}
        </div>
      )}

      {state.isFailed && (
        <RequestErrorNotice title="Não foi possível buscar os funcionários" error={state.error} />
      )}

      {state.isSuccess && state.data.total === 0 && (
        <Notice
          type="info"
          title="Nada encontrado"
          message={`Nenhum funcionário ativo ${urlCep ? `no CEP ${urlCep}` : ''}`}
          description="Confira se está tudo certo. Funcionários inativos não aparecem nesta busca."
        />
      )}

      {state.isSuccess && state.data.total > 0 && (
        <>
          <EmployeeTable employees={state.data.items} />
          <Pagination
            count={`${state.data.total} ${state.data.total === 1 ? 'funcionário ativo' : 'funcionários ativos'}${urlCep ? ` no CEP ${urlCep}` : ''}`}
            offset={offset}
            pageSize={limit}
            total={state.data.total}
            onChange={changePage}
          />
        </>
      )}
    </main>
  );
}

export default EmployeesPage;
