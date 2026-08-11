// Shared by BookingContext (new-booking estimate) and StaffReturnPage (the
// actual return invoice preview) so both price a rental the same way: pack
// whole months first, then whole weeks out of what's left, then bill the
// remaining days at the daily rate. Mirrors the backend's
// RentalRateCalculator.
export function calculateRentalAmount(carType, days) {
  if (!carType) return 0;
  const dailyRate = Number(carType.dailyRate || 0);
  const weeklyRate = Number(carType.weeklyRate || 0);
  const monthlyRate = Number(carType.monthlyRate || 0);

  let remainingDays = days;
  let amount = 0;

  if (monthlyRate > 0 && remainingDays >= 30) {
    const months = Math.floor(remainingDays / 30);
    amount += months * monthlyRate;
    remainingDays %= 30;
  }
  if (weeklyRate > 0 && remainingDays >= 7) {
    const weeks = Math.floor(remainingDays / 7);
    amount += weeks * weeklyRate;
    remainingDays %= 7;
  }
  amount += remainingDays * dailyRate;

  return amount;
}

// Human-readable breakdown of how `days` splits into months/weeks/days for
// the given rates — e.g. "1 month + 2 days" — so the UI doesn't always say
// "N days" even when the amount was actually billed at the weekly/monthly
// tier. Mirrors the same packing calculateRentalAmount does.
export function describeRentalDuration(carType, days) {
  const weeklyRate = Number(carType?.weeklyRate || 0);
  const monthlyRate = Number(carType?.monthlyRate || 0);

  let remainingDays = days;
  const parts = [];

  if (monthlyRate > 0 && remainingDays >= 30) {
    const months = Math.floor(remainingDays / 30);
    parts.push(`${months} month${months > 1 ? 's' : ''}`);
    remainingDays %= 30;
  }
  if (weeklyRate > 0 && remainingDays >= 7) {
    const weeks = Math.floor(remainingDays / 7);
    parts.push(`${weeks} week${weeks > 1 ? 's' : ''}`);
    remainingDays %= 7;
  }
  if (remainingDays > 0 || parts.length === 0) {
    parts.push(`${remainingDays} day${remainingDays > 1 ? 's' : ''}`);
  }

  return parts.join(' + ');
}

// Fuel is tracked in quarters of a tank (25/50/75/100). Charged only when
// the car comes back with less fuel than it was handed over with — e.g.
// handed over Full (100), returned at Half (50) is 2 quarters short, so
// 2 x FUEL_CHARGE_PER_QUARTER. Extra fuel left in the tank isn't credited.
// Mirrors the backend's StaffServiceImpl.processReturn.
const FUEL_LEVEL_PER_QUARTER = 25;
const FUEL_CHARGE_PER_QUARTER = 2;

export function calculateFuelCharge(handoverFuelLevel, returnFuelLevel) {
  if (handoverFuelLevel == null || returnFuelLevel == null) return 0;
  const quartersShort = Math.max(0, Math.floor((handoverFuelLevel - returnFuelLevel) / FUEL_LEVEL_PER_QUARTER));
  return quartersShort * FUEL_CHARGE_PER_QUARTER;
}

// Re-prices an add-on line against the days actually billed rather than
// trusting `line.subtotal`, which was frozen at booking time against the
// originally planned stay — on an early return that's now longer than the
// actual days, so it needs re-pricing the same way calculateRentalAmount is.
// Mirrors the backend's StaffServiceImpl.processReturn.
export function calculateAddonLineAmount(line, days) {
  return Number(line.addonRate || 0) * Number(line.quantity || 0) * days;
}
