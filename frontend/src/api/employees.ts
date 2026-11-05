import { ApiError } from './ApiError';

export type EmployeeType = 'PERMANENT' | 'FIXED_TERM' | 'INTERN' | 'SCHOLAR';

export type Employee = {
  socialNumber: { number: string };
  type: EmployeeType;
  firstName: string;
  lastName: string;
  baseSalary: number;
  address: string | null;
};

export type EmployeePage = {
  items: Employee[];
  total: number;
};

// limit e offset são sempre enviados: sem eles, a API responde com erro 500.
export async function findActiveEmployeesByCep(
  cep: string,
  limit: number,
  offset: number,
): Promise<EmployeePage> {
  const params = new URLSearchParams({
    cep,
    limit: String(limit),
    offset: String(offset),
  });
  const response = await fetch(`/api/employees?${params}`);

  if (!response.ok) {
    throw new ApiError(response.status);
  }

  return response.json();
}
