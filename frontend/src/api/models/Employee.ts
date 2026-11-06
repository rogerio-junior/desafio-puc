export type EmployeeType = 'PERMANENT' | 'FIXED_TERM' | 'INTERN' | 'SCHOLAR';

export type Gender = 'M' | 'F' | 'N';

export type Employee = {
  id: string;
  type: EmployeeType;
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: Gender;
  baseSalary: number;
  address: string | null;
};
