import { api } from './client';

// Reference data (states, cities, hubs, airports) barely ever changes at
// runtime, so it's fetched once and cached here — this is what lets
// getHubById/getCityById/getStateById/findHubForCity/findHubForAirport stay
// PLAIN SYNCHRONOUS functions, exactly as every existing page already calls
// them (see ConfirmPage.jsx's `getHubById(...)` used directly in render,
// and ModifyCancelPage.jsx's `getCityById(...)`/`getStateById(...)`).
// preloadReferenceData() (called once from App.jsx on mount) is what fills
// these arrays; until it resolves the sync helpers simply return undefined,
// same as an unmatched .find() already did in the old mock.
let statesCache = [];
let citiesCache = [];
let hubsCache = [];
let airportsCache = [];

// GET /api/states
export async function getStates() {
  const data = await api.get('/states');
  statesCache = data;
  return data;
}

// GET /api/cities?stateId=
export async function getCitiesByState(stateId) {
  if (!stateId) return [];
  const data = await api.get('/cities', { params: { stateId } });
  // Merge rather than replace — different states' cities accumulate in the
  // same cache as the user browses, instead of overwriting each other.
  const others = citiesCache.filter((c) => c.stateId !== Number(stateId));
  citiesCache = [...others, ...data];
  return data;
}

// GET /api/cities (no stateId) — full list, used only to warm the cache.
async function getAllCitiesForPreload() {
  const data = await api.get('/cities');
  citiesCache = data;
  return data;
}

// GET /api/hubs — full list, used to warm the cache the sync helpers read.
export async function getAllHubs() {
  const data = await api.get('/hubs');
  hubsCache = data;
  return data;
}

// GET /api/airports — full list, same idea as getAllHubs().
export async function getAllAirports() {
  const data = await api.get('/airports');
  airportsCache = data;
  return data;
}

// GET /api/airports/search?q= — the live-typing search box in
// BookingSearchCard; deliberately a separate, truncated endpoint from
// getAllAirports() above.
export async function searchAirports(query) {
  const data = await api.get('/airports/search', { params: { q: query } });
  return data;
}

// One-time warm-up, called from App.jsx on mount. Fetches everything the
// synchronous helpers below need before any page is likely to call them.
export async function preloadReferenceData() {
  await Promise.all([getStates(), getAllCitiesForPreload(), getAllHubs(), getAllAirports()]);
}

// GET /api/airports/:id
export async function getAirportById(airportId) {
  return api.get(`/airports/${airportId}`);
}

// POST /api/airports — Airport Master create.
// payload: { airportName, airportCode, cityId, stateId, hubId }
export async function createAirport(payload) {
  return api.post('/airports', payload);
}

// PUT /api/airports/:id — Airport Master update (full replace of the
// editable fields, not a partial patch — matches the DB design's Airport
// Master, where every field is required/NOT NULL).
export async function updateAirport(airportId, payload) {
  return api.put(`/airports/${airportId}`, payload);
}

// DELETE /api/airports/:id
export async function deleteAirport(airportId) {
  return api.delete(`/airports/${airportId}`);
}

// --- Synchronous lookups over the caches above — same signatures as the
// original mock, so no page needed to change. ---

export function findHubForCity(cityId) {
  return hubsCache.filter((h) => h.cityId === Number(cityId));
}

export function findHubForAirport(airportId) {
  const airport = airportsCache.find((a) => a.airportId === Number(airportId));
  if (!airport) return undefined;
  return hubsCache.find((h) => h.hubId === airport.hubId);
}

export function getHubById(hubId) {
  return hubsCache.find((h) => h.hubId === Number(hubId));
}

export function getCityById(cityId) {
  return citiesCache.find((c) => c.cityId === Number(cityId));
}

export function getStateById(stateId) {
  return statesCache.find((s) => s.stateId === Number(stateId));
}
