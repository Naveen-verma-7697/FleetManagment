import { api } from './client';

// POST /api/auth/login  { email, password } -> { token, customer }
export async function login({ email, password }) {
  return api.post('/auth/login', { email, password });
}

// POST /api/auth/register -> { token, customer }
export async function register({ fullName, email, password }) {
  return api.post('/auth/register', { fullName, email, password });
}

// POST /api/auth/staff-login -> { token, staff }
export async function staffLogin({ username, password }) {
  return api.post('/auth/staff-login', { username, password });
}

// PUT /api/customers  (body carries customerId — a partial patch, any
// omitted field is left unchanged server-side)
export async function updateCustomer(patch) {
  return api.put('/customers', patch);
}

// GET /api/customers/:id
export async function getCustomerById(customerId) {
  return api.get(`/customers/${customerId}`);
}

// POST /api/customers/guest — used by the booking flow for guests who fill
// in their details without logging in first; finds or creates a Customer
// row so the booking always carries a real customerId.
export async function upsertGuestCustomer(info) {
  return api.post('/customers/guest', info);
}

// GET /api/customers/:id/govt-id — the actual base64 document, fetched
// separately from getCustomerById so a normal profile read doesn't have to
// carry the whole file every time.
export async function getGovtIdDocument(customerId) {
  return api.get(`/customers/${customerId}/govt-id`);
}

/////sso/////
  // GET /api/customers/me
export async function getCurrentCustomer() {
  return api.get("/customers/me");
}