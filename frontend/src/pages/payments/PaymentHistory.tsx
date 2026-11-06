import type { HistoryPayment, PaymentEmployee } from '../../api/models/Payment';
import { formatCpf, formatCurrency } from '../../utils/format';
import { getMonthName } from '../../utils/payrollPeriod';
import styles from './PaymentHistory.module.css';

type PaymentHistoryProps = {
  employee: PaymentEmployee;
  payments: HistoryPayment[];
};

function capitalize(text: string) {
  return text.charAt(0).toUpperCase() + text.slice(1);
}

function PaymentHistory({ employee, payments }: PaymentHistoryProps) {
  return (
    <section className={`rise ${styles.history}`}>
      <div className={styles.employee}>
        <span className={styles.name}>
          {employee.firstName} {employee.lastName}
        </span>
        <span className={styles.cpf}>CPF {formatCpf(employee.id)}</span>
      </div>
      <div className={`${styles.columns} ${styles.header}`}>
        <span>Ano</span>
        <span>Mês</span>
        <span className={styles.salaryHeader}>Salário a receber</span>
      </div>
      {payments.map((payment, index) => (
        <div key={`${payment.year}-${payment.month}`} className={`${styles.columns} ${styles.row}`}>
          {/* O ano só aparece na primeira linha de cada ano. */}
          <span className={styles.year}>
            {index === 0 || payments[index - 1].year !== payment.year ? payment.year : ''}
          </span>
          <span>{capitalize(getMonthName(`${payment.year}-${payment.month}`))}</span>
          <span className={styles.salary}>{formatCurrency(payment.amount)}</span>
        </div>
      ))}
    </section>
  );
}

export default PaymentHistory;
