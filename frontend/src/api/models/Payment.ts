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

export type EmployeePayment = {
  employeeId: string;
  firstName: string;
  lastName: string;
  year: number;
  month: number;
  amount: number;
};

export type PaymentHistoryEntry = {
  year: number;
  month: number;
  amount: number;
};

export type PaymentHistoryEmployee = {
  id: string;
  firstName: string;
  lastName: string;
  active: boolean;
};

export type PaymentHistory = {
  employee: PaymentHistoryEmployee;
  payments: Page<PaymentHistoryEntry>;
};
