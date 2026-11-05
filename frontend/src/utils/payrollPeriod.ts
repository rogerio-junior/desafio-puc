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

export function getMonthName(period: string) {
  const month = Number(period.split('-')[1]);
  return MONTH_NAMES[month - 1];
}

export function formatPeriodRange(period: string) {
  const [year, month] = period.split('-').map(Number);
  const lastDay = new Date(year, month, 0).getDate();

  return `1 a ${lastDay} de ${getMonthName(period)} de ${year}`;
}
