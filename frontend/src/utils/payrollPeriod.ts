const MONTH_NAMES = [
  'janeiro',
  'fevereiro',
  'março',
  'abril',
  'maio',
  'junho',
  'julho',
  'agosto',
  'setembro',
  'outubro',
  'novembro',
  'dezembro',
];

function pad(value: number, length = 2) {
  return String(value).padStart(length, '0');
}

// Usa a data local: toISOString() converte para UTC e pode retornar o dia seguinte.
export function getToday() {
  const now = new Date();
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`;
}

// A folha calculada é sempre a do mês anterior ao da data de referência.
export function getPayrollPeriod(referenceDate: string) {
  const [year, month] = referenceDate.split('-').map(Number);

  return month === 1
    ? `${pad(year - 1, 4)}-12`
    : `${pad(year, 4)}-${pad(month - 1)}`;
}

// Recusa também datas que não existem, como 2026-02-31.
export function isValidDate(value: string) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false;

  const [year, month, day] = value.split('-').map(Number);
  const date = new Date(year, month - 1, day);

  return date.getMonth() === month - 1 && date.getDate() === day;
}

export function isValidPeriod(period: string) {
  return /^\d{4}-(0[1-9]|1[0-2])$/.test(period);
}

export function getMonthName(period: string) {
  const month = Number(period.split('-')[1]);
  return MONTH_NAMES[month - 1];
}

export function formatPeriodRange(period: string) {
  const [year, month] = period.split('-').map(Number);
  const lastDay = new Date(year, month, 0).getDate();

  return `1 a ${lastDay} de ${getMonthName(period)} de ${year}`;
}
