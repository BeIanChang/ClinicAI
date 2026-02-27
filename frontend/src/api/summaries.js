const SUMMARY_BASE_URL = import.meta.env.VITE_SUMMARY_BASE_URL;

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

export const generateSummary = (payload, token) =>
  fetch(`${SUMMARY_BASE_URL}/api/summaries/generate`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  }).then(handleResponse);
