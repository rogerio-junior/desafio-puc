import type { Page } from '../models/Page';
import type {
  ComputeSalaryResult,
  EmployeePayment,
  PaymentHistory,
} from '../models/Payment';
import { ApiError } from './ApiError';

export async function computeSalary(referenceDate: string): Promise<ComputeSalaryResult> {
  const response = await fetch('/api/payments/calculate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ referenceDate })
  });

  if (!response.ok) {
    throw await ApiError.from(response);
  }

  return response.json();
}

export async function findPaymentsByMonth(
  month: string,
  limit: number,
  offset: number,
): Promise<Page<EmployeePayment>> {
  const params = new URLSearchParams({
    month,
    limit: String(limit),
    offset: String(offset),
  });

  const response = await fetch(`/api/payments?${params}`, {
    headers: { Accept: 'application/json' },
  });

  if (!response.ok) {
    throw await ApiError.from(response);
  }

  return response.json();
}

export async function getPaymentHistory(
  cpf: string,
  month: string,
  limit: number,
  offset: number,
): Promise<PaymentHistory> {
  const params = new URLSearchParams({ limit: String(limit), offset: String(offset) });
  if (month) params.set('month', month);

  const response = await fetch(`/api/employees/${cpf}/payments?${params}`, {
    headers: { Accept: 'application/json' },
  });

  if (!response.ok) {
    throw await ApiError.from(response);
  }

  return response.json();
}

export async function fetchPaymentsCsv(month: string): Promise<Blob> {
  const params = new URLSearchParams({ month });
  const response = await fetch(`/api/payments?${params}`, {
    headers: { Accept: 'text/csv' },
  });

  if (!response.ok) {
    throw await ApiError.from(response);
  }

  return response.blob();
}
