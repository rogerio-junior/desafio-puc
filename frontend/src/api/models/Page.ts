// Espelha o Page do backend, usado por todos os endpoints paginados.
export type Page<T> = {
  items: T[];
  total: number;
};
