import { Link } from 'react-router';
import type { Employee, EmployeeType, Gender } from '../../api/models/Employee';
import { formatCpf, formatCurrency, formatDate } from '../../utils/format';
import styles from './EmployeeTable.module.css';

const EMPLOYEE_TYPE_LABELS: Record<EmployeeType, string> = {
  PERMANENT: 'Tempo indeterminado',
  FIXED_TERM: 'Contrato a prazo',
  INTERN: 'Estágio',
  SCHOLAR: 'Bolsa',
};

const GENDER_LABELS: Record<Gender, string> = {
  M: 'Masculino',
  F: 'Feminino',
  N: 'Não binário',
};

type EmployeeTableProps = {
  employees: Employee[];
};

function EmployeeTable({ employees }: EmployeeTableProps) {
  return (
    <div className={`rise ${styles.scroll}`}>
      <div className={styles.table}>
        <div className={`${styles.columns} ${styles.header}`}>
          <span>Funcionário</span>
          <span>CPF</span>
          <span>Nascimento</span>
          <span>Gênero</span>
          <span>Contrato</span>
          <span>Endereço</span>
          <span className={styles.salaryHeader}>Salário base</span>
        </div>
        {employees.map((employee) => (
          <div
            key={employee.socialNumber.number}
            className={`${styles.columns} ${styles.row}`}
          >
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
            <span className={styles.cell}>{formatCpf(employee.socialNumber.number)}</span>
            <span className={styles.cell}>{formatDate(employee.birthDate)}</span>
            <span className={styles.cell}>{GENDER_LABELS[employee.gender]}</span>
            <span className={styles.cell}>{EMPLOYEE_TYPE_LABELS[employee.type]}</span>
            <span className={`${styles.cell} ${styles.address}`}>
              {employee.address || 'Endereço não informado'}
            </span>
            <span className={styles.salary}>{formatCurrency(employee.baseSalary)}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default EmployeeTable;
