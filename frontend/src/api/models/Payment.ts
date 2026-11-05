import type { Page } from './Page';

// TODO: Melhorar esse retorno, deixar o payrollRef plano
export type ComputeSalaryResult = {
  payrollRef: {
    referenceDate: string;
    period: string;
  };
  expectedQuantity: number;
  generatedQuantity: number;
};

export type MonthPayment = {
  idEmployee: string;
  firstName: string;
  lastName: string;
  year: number;
  month: number;
  salary: number;
};

export type HistoryPayment = {
  year: number;
  month: number;
  salaryToReceive: number;
};

export type PaymentEmployee = {
  id: string;
  firstName: string;
  lastName: string;
  active: boolean;
};

export type EmployeePayments = {
  employee: PaymentEmployee;
  paymentsPage: Page<HistoryPayment>;
};
