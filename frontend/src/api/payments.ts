import { ApiError } from './ApiError';

export type ComputeSalaryResult = {
  payrollRef: {
    referenceDate: string;
    period: string;
  };
  expectedQuantity: number;
  generatedQuantity: number;
};

export async function computeSalary(referenceDate: string): Promise<ComputeSalaryResult> {
  const response = await fetch('/api/payments/calculate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ referenceDate })
  });

  if (!response.ok) {
    throw new ApiError(response.status);
  }

  return response.json();
}
