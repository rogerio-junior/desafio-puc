const MAX_LIMIT = 500;
const MAX_OFFSET = 2147483647;

// Retorna null quando a URL não define o limit: cada tela usa o seu tamanho padrão.
export function readLimit(params: URLSearchParams) {
  const limit = params.get('limit') ?? '';
  return /^\d+$/.test(limit) && Number(limit) >= 1 && Number(limit) <= MAX_LIMIT
    ? Number(limit)
    : null;
}

export function readOffset(params: URLSearchParams) {
  const offset = params.get('offset') ?? '';
  return /^\d+$/.test(offset) && Number(offset) <= MAX_OFFSET ? Number(offset) : 0;
}

// Só vão para a URL os valores diferentes do padrão, para mantê-la curta.
export function setPaginationParams(params: URLSearchParams, limit: number | null, offset: number) {
  if (limit !== null) params.set('limit', String(limit));
  if (offset > 0) params.set('offset', String(offset));
}

export function getLastPageOffset(total: number, limit: number) {
  return Math.floor((total - 1) / limit) * limit;
}
