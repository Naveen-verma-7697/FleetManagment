export function formatCurrency(amount) {
  if (amount === null || amount === undefined || Number.isNaN(amount)) return '₹0';
  // en-IN gives the Indian digit-grouping convention (lakh/crore commas,
  // e.g. ₹1,00,000 not ₹100,000) — correct for an India-based deployment.
  return `₹${Number(amount).toLocaleString('en-IN', { maximumFractionDigits: 0 })}`;
}

export function formatDateShort(isoDateOrString) {
  if (!isoDateOrString) return '';
  const d = new Date(isoDateOrString);
  if (Number.isNaN(d.getTime())) return '';
  return d.toLocaleDateString('en-IN', { month: 'short', day: 'numeric' });
}

export function formatDateTime(isoDateOrString) {
  if (!isoDateOrString) return '';
  const d = new Date(isoDateOrString);
  if (Number.isNaN(d.getTime())) return '';
  return d.toLocaleString('en-IN', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  });
}
