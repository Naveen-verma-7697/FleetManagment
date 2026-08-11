import { api } from './client';

// GET /api/car-types?hubId=&pickupDatetime=&returnDatetime=
// hubId/dates are optional — omit them for the full catalog (e.g. an
// admin view), or pass them to get every category back with a live
// availableCount for that hub/window. A type with availableCount 0 is
// still returned (not filtered out) so the vehicle-selection page can
// show it disabled with an "Unavailable" badge instead of it silently
// disappearing from the list.
export async function getCarTypes({ hubId, pickupDatetime, returnDatetime } = {}) {
  return api.get('/car-types', { params: { hubId, pickupDatetime, returnDatetime } });
}

// GET /api/cars/available?hubId=&pickupDatetime=&returnDatetime=
//
// Not used by the booking flow anymore — a booking only reserves a
// category (see getCarTypes), and pickupDatetime/returnDatetime no longer
// affect a specific car's availability the way they affect a category's.
// Kept for other per-car views (e.g. a future admin/fleet browser); dates
// are accepted but currently ignored server-side for this endpoint.
export async function getAvailableCars({ hubId, pickupDatetime, returnDatetime } = {}) {
  return api.get('/cars/available', {
    params: { hubId, pickupDatetime, returnDatetime },
  });
}

// GET /api/cars/:id
export async function getCarById(carId) {
  return api.get(`/cars/${carId}`);
}
