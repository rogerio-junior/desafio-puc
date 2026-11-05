export class ApiError extends Error {
  readonly status: number;

  constructor(status: number) {
    super(`A API respondeu com o status ${status}`);
    this.status = status;
  }
}
