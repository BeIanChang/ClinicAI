const AUTH_BASE_URL = import.meta.env.VITE_AUTH_BASE_URL;

const parseJson = async (response) => {
  const text = await response.text();
  if (!text) {
    return null;
  }
  try {
    return JSON.parse(text);
  } catch (error) {
    return { message: text };
  }
};

const handleResponse = async (response) => {
  if (!response.ok) {
    const errorBody = await parseJson(response);
    const message = errorBody?.message || `Request failed with ${response.status}`;
    throw new Error(message);
  }
  return parseJson(response);
};

export const register = (payload) =>
  fetch(`${AUTH_BASE_URL}/api/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  }).then(handleResponse);

export const login = (payload) =>
  fetch(`${AUTH_BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  }).then(handleResponse);

export const validateToken = (token) =>
  fetch(`${AUTH_BASE_URL}/api/auth/validate`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  }).then(handleResponse);
