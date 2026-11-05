const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
});

export function formatCurrency(value: number) {
  return currencyFormatter.format(value);
}

export function formatCep(value: string) {
  const digits = value.replace(/\D/g, '').slice(0, 8);
  return digits.length > 5 ? `${digits.slice(0, 5)}-${digits.slice(5)}` : digits;
}

export function formatCpf(value: string) {
  const digits = value.replace(/\D/g, '').slice(0, 11);
  let formatted = digits.slice(0, 3);

  if (digits.length > 3) formatted += `.${digits.slice(3, 6)}`;
  if (digits.length > 6) formatted += `.${digits.slice(6, 9)}`;
  if (digits.length > 9) formatted += `-${digits.slice(9)}`;

  return formatted;
}

// Recebe a data no formato da API (aaaa-mm-dd).
export function formatDate(date: string) {
  return date.split('-').reverse().join('/');
}

// Mesmo comportamento dos campos nativos de data: 3 vira 03, e 13 não entra como mês.
export function formatMonthInput(value: string) {
  let digits = value.replace(/\D/g, '').slice(0, 6);

  if (digits[0] > '1') digits = `0${digits}`.slice(0, 6);

  const month = Number(digits.slice(0, 2));
  if (digits.length >= 2 && (month === 0 || month > 12)) {
    digits = digits[0] + digits.slice(2);
  }

  return digits.length > 2 ? `${digits.slice(0, 2)}/${digits.slice(2)}` : digits;
}

export function isCompleteMonthInput(value: string) {
  return /^(0[1-9]|1[0-2])\/\d{4}$/.test(value);
}

// Converte de mm/aaaa para o formato da API (aaaa-mm).
export function monthInputToPeriod(value: string) {
  return `${value.slice(3)}-${value.slice(0, 2)}`;
}

export function periodToMonthInput(period: string) {
  return `${period.slice(5, 7)}/${period.slice(0, 4)}`;
}
