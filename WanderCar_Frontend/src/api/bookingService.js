import { api } from './client';

// POST /api/bookings
// payload: { customerId, carTypeId, pickupHubId, dropHubId, pickupDatetime,
//            returnDatetime, addons: [{ addonId, quantity }], estimatedAmount, remarks }
//
// carTypeId reserves a category, not a specific vehicle — the actual car
// is assigned later by staff at hand-over. estimatedAmount is sent for
// display continuity only — the server recomputes rental + add-on totals
// itself from current rates and never trusts a client-submitted price.
export async function createBooking(payload) {
  return api.post('/bookings', payload);
}

// GET /api/bookings/:confirmationNo
export async function getBookingByConfirmation(confirmationNo) {
  return api.get(`/bookings/confirmation/${confirmationNo}`);
}

// GET /api/bookings/me?customerId=
// export async function getLastBookingForCustomer(customerId) {
//   return api.get('/bookings/me', { params: { customerId } });
// }

export async function getLastBookingForCustomer() {
    return api.get("/bookings/me");
}

// GET /api/bookings?customerId= — every booking for the customer, not just
// the last one. Backs the "modify booking" page's picker: a logged-in
// customer sees all their bookings and chooses which to change.
// export async function getBookingsForCustomer(customerId) {
//   return api.get('/bookings', { params: { customerId } });
// }

export async function getBookingsForCustomer() {
    return api.get("/bookings");
}

// PUT /api/bookings/:confirmationNo
export async function modifyBooking(confirmationNo, patch) {
  return api.put(`/bookings/confirmation/${confirmationNo}`, patch);
}

// DELETE /api/bookings/:confirmationNo
export async function cancelBooking(confirmationNo) {
  return api.delete(`/bookings/confirmation/${confirmationNo}`);
}
