import { Link } from 'react-router';
import type { Employee, EmployeeType } from '../../api/employees';
import { formatCurrency } from '../../utils/format';
import styles from './EmployeeList.module.css';

const EMPLOYEE_TYPE_LABELS: Record<EmployeeType, string> = {
  PERMANENT: 'Tempo indeterminado',
  FIXED_TERM: 'Contrato a prazo',
  INTERN: 'Estágio',
  SCHOLAR: 'Bolsa',
};

type EmployeeListProps = {
  title: string;
  employees: Employee[];
};

function EmployeeList({ title, employees }: EmployeeListProps) {
  return (
    <div className={`rise ${styles.list}`}>
      <p className={styles.title}>{title}</p>
      <div className={styles.rule} />
      {employees.map((employee) => (
        <div key={employee.socialNumber.number} className={styles.row}>
          <div className={styles.nameCell}>
            <span className={styles.name}>
              {employee.firstName} {employee.lastName}
            </span>
            <Link
              className={styles.paymentsLink}
              to={`/pagamentos?cpf=${employee.socialNumber.number}`}
            >
              Ver todos os pagamentos
            </Link>
          </div>
          <span className={styles.salary}>{formatCurrency(employee.baseSalary)}</span>
          <span className={styles.detail}>
            {employee.address || 'Endereço não informado'}
          </span>
          <span className={`${styles.detail} ${styles.type}`}>
            {EMPLOYEE_TYPE_LABELS[employee.type]}
          </span>
        </div>
      ))}
    </div>
  );
}

export default EmployeeList;
