export class ApiError extends Error {
  readonly status: number;
  readonly detail: string | null;

  constructor(status: number, detail: string | null = null) {
    super(detail ?? `A API respondeu com o status ${status}`);
    this.status = status;
    this.detail = detail;
  }

  // Lê o detail do ProblemDetail (RFC 9457) devolvido pela API, quando houver.
  static async from(response: Response) {
    let detail: string | null = null;

    try {
      const body = await response.json();
      if (typeof body?.detail === 'string') detail = body.detail;
    } catch {
      // Resposta sem corpo JSON: fica só o status.
    }

    return new ApiError(response.status, detail);
  }
}

// Falha de rede (o fetch rejeita) ou erro do servidor: o problema não está na requisição.
export function isUnavailableError(error: unknown) {
  return !(error instanceof ApiError) || error.status >= 500;
}
