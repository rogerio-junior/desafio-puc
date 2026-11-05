import type { ComputeSalaryResult } from '../../api/payments';
import { useCountUp } from '../../hooks/useCountUp';
import { getMonthName } from '../../utils/payrollPeriod';
import styles from './SalaryResult.module.css';

type SalaryResultProps = {
  result: ComputeSalaryResult;
};

function SalaryResult({ result }: SalaryResultProps) {
  const { payrollRef, expectedQuantity, generatedQuantity } = result;
  const shownQuantity = useCountUp(generatedQuantity);

  return (
    <div className={`rise ${styles.result}`}>
      <p className={styles.title}>Salários calculados</p>
      <div className={styles.rule} />
      <div className={styles.totals}>
        <div className={styles.total}>
          <span className={styles.totalLabel}>Esperado</span>
          <span className={styles.totalValue}>{expectedQuantity}</span>
        </div>
        <div className={styles.total}>
          <span className={styles.totalLabel}>Total calculado</span>
          <span className={`${styles.totalValue} ${styles.confirmed}`}>{shownQuantity}</span>
        </div>
      </div>

      {generatedQuantity === 0 && (
        <p className={styles.nothing}>
          Nenhum funcionário ativo está pendente neste período. Pagamentos
          existentes não são sobrescritos.
        </p>
      )}

      <a
        className={styles.link}
        href={`/pagamentos?month=${payrollRef.period}`}
      >
        Ver pagamentos de {getMonthName(payrollRef.period)} →
      </a>
    </div>
  );
}

export default SalaryResult;
