import { api } from './client';

// GET /api/addons
export async function getAddons() {
  return api.get('/addons');
}
