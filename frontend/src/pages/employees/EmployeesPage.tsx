import { useState, type ChangeEvent, type FormEvent } from 'react';
import { findActiveEmployeesByCep } from '../../api/employees';
import Button from '../../components/Button';
import Field from '../../components/Field';
import Notice from '../../components/Notice';
import Pagination from '../../components/Pagination';
import Skeleton from '../../components/Skeleton';
import { useApiCall } from '../../hooks/useApiCall';
import { formatCep } from '../../utils/format';
import EmployeeList from './EmployeeList';
import styles from './EmployeesPage.module.css';

const PAGE_SIZE = 10;
const SKELETON_ROWS = [1, 2, 3];

const BUTTON_LABELS = {
  initial: 'Buscar',
  success: 'Buscar',
  loading: 'Buscando…',
  failed: 'Tentar novamente',
};

function EmployeesPage() {
  const [cep, setCep] = useState('');
  const [validationError, setValidationError] = useState('');
  const [query, setQuery] = useState({ cep: '', offset: 0 });
  const { state, call, reset } = useApiCall(findActiveEmployeesByCep);

  function search(searchCep: string, offset: number) {
    setQuery({ cep: searchCep, offset });
    call(searchCep, PAGE_SIZE, offset);
  }

  function handleCepChange(event: ChangeEvent<HTMLInputElement>) {
    setCep(formatCep(event.target.value));
    setValidationError('');
    if (state.isFailed) reset();
  }

  function handleClear() {
    setCep('');
    setValidationError('');
    reset();
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (cep && !/^\d{5}-\d{3}$/.test(cep)) {
      setValidationError('Use o formato 00000-000.');
      return;
    }

    setValidationError('');
    // Ao tentar de novo depois de uma falha, continua na mesma página da lista.
    search(cep, state.isFailed ? query.offset : 0);
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
        <Notice
          type="error"
          title="Não foi possível buscar os funcionários"
          message="O sistema está indisponível"
          description="Tente novamente em alguns minutos."
        />
      )}

      {state.isSuccess && state.data.total === 0 && (
        <Notice
          type="info"
          title="Nada encontrado"
          message={`Nenhum funcionário ativo ${query.cep ? 'no CEP' + query.cep : ''}`}
          description="Confira se está tudo certo. Funcionários inativos não aparecem nesta busca."
        />
      )}

      {state.isSuccess && state.data.total > 0 && (
        <>
          <EmployeeList
            title={`${state.data.total} ${state.data.total === 1 ? 'funcionário ativo' : 'funcionários ativos'} no CEP ${query.cep}`}
            employees={state.data.items}
          />
          {state.data.total > PAGE_SIZE && (
            <Pagination
              offset={query.offset}
              pageSize={PAGE_SIZE}
              itemCount={state.data.items.length}
              total={state.data.total}
              onChange={(offset) => search(query.cep, offset)}
            />
          )}
        </>
      )}
    </main>
  );
}

export default EmployeesPage;
