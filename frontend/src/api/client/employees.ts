import type { EmployeeCep } from '../models/Employee';
import type { Page } from '../models/Page';
import { ApiError } from './ApiError';

export async function findActiveEmployeesByCep(
  cep: string,
  limit: number,
  offset: number,
): Promise<Page<EmployeeCep>> {
  const params = new URLSearchParams({
    cep,
    limit: String(limit),
    offset: String(offset),
  });

  const response = await fetch(`/api/employees?${params}`, {
    headers: { Accept: 'application/json' },
  });

  if (!response.ok) {
    throw await ApiError.from(response);
  }

  return response.json();
}
