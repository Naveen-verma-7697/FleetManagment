import axios from 'axios';

// Real axios instance pointed at the Spring Boot backend, replacing the
// in-memory delay()/mockDb.js layer this file used to provide. Every
// service file below imports `api` from here instead of talking to axios
// directly — one place to change the base URL, timeouts, or headers later.
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

// Unwraps response.data automatically and normalises every failure into
// the same ApiError shape every page's catch block already expects
// (err.message), so no calling code needed to change when the mock was
// swapped out for real HTTP calls.

// add for auth token 
api.interceptors.request.use((config) => {

  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message =
      error.response?.data?.message || error.message || 'Something went wrong. Please try again.';
    const status = error.response?.data?.status ?? error.response?.status ?? 500;
    return Promise.reject(new ApiError(message, status));
  }
);

// Same shape as before — every page already does `err.message` in its
// catch blocks, so this class didn't need to change at all.
export class ApiError extends Error {
  constructor(message, status = 400) {
    super(message);
    this.status = status;
  }
}
