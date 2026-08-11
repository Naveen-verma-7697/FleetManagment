import { api } from './client';

// GET /api/staff/bookings/:confirmationNo/available-cars — every car of
// this booking's reserved category, at its pickup hub, that's physically
// free right now. The hand-over screen calls this after looking up a
// confirmation number, to build the picker list.
export async function getAvailableCarsForHandover(confirmationNo) {
  return api.get(`/staff/bookings/${confirmationNo}/available-cars`);
}

// Staff: hand a specific vehicle over to the customer at pickup. carId is
// the one the staff member picked from getAvailableCarsForHandover's list —
// the booking only reserved a category until this call assigns an actual car.
export async function handoverVehicle(confirmationNo, { carId, fuelStatus, notes } = {}) {
  return api.post(`/staff/bookings/${confirmationNo}/handover`, { carId, fuelStatus, notes });
}

// Staff: process a return — closes the booking, frees the car, generates an
// invoice. paymentType left undefined/null means "not collected yet" — the
// backend records that as payment_status = PENDING rather than PAID.
export async function processReturn(
  confirmationNo,
  { extraMiles = 0, extraChargeAmount = 0, damageNotes = '', fuelStatus, paymentType } = {}
) {
  return api.post(`/staff/bookings/${confirmationNo}/return`, {
    extraMiles,
    extraChargeAmount,
    damageNotes,
    fuelStatus,
    paymentType,
  });
}

// Staff: cancellation lookup shares the same record shape as the customer flow.
export { getBookingByConfirmation, cancelBooking } from './bookingService';

// GET /api/staff/bookings — every booking, for the staff booking page:
// customer info, car (type + registration number), confirmation no, etc.
export async function getAllBookings() {
  return api.get('/staff/bookings');
}

// GET /api/staff/dashboard?hubId= — per car type, how many are available /
// handed over / in maintenance right now. hubId is optional; omit it for
// a fleet-wide view.
export async function getDashboard(hubId) {
  return api.get('/staff/dashboard', { params: { hubId } });
}
