export type EmployeeType = 'PERMANENT' | 'FIXED_TERM' | 'INTERN' | 'SCHOLAR';

export type Gender = 'M' | 'F' | 'N';

// Funcionário com o CEP extraído do endereço (null quando o endereço não tem CEP).
export type EmployeeCep = {
  cep: string | null;
  employeeId: string;
  type: EmployeeType;
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: Gender;
  baseSalary: number;
  active: boolean;
};
