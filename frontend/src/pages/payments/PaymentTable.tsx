import { Link } from 'react-router';
import type { MonthPayment } from '../../api/models/Payment';
import { formatCpf, formatCurrency } from '../../utils/format';
import styles from './PaymentTable.module.css';

type PaymentTableProps = {
  payments: MonthPayment[];
};

function PaymentTable({ payments }: PaymentTableProps) {
  return (
    <div className={`rise ${styles.table}`}>
      <div className={`${styles.columns} ${styles.header}`}>
        <span>Funcionário</span>
        <span className={styles.cpfHeader}>CPF</span>
        <span className={styles.salaryHeader}>Salário a receber</span>
      </div>
      {payments.map((payment) => (
        <div key={payment.idEmployee} className={`${styles.columns} ${styles.row}`}>
          <span className={styles.name}>
            {payment.firstName} {payment.lastName}
          </span>
          <span className={styles.cpf}>
            <span>{formatCpf(payment.idEmployee)}</span>
            <Link className={styles.historyLink} to={`/pagamentos?cpf=${payment.idEmployee}`}>
              Ver todos os pagamentos
            </Link>
          </span>
          <span className={styles.salary}>{formatCurrency(payment.salary)}</span>
        </div>
      ))}
    </div>
  );
}

export default PaymentTable;
