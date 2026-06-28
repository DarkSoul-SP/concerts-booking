const API_BASE = '/api'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  })

  if (response.status === 204) {
    return null
  }

  const contentType = response.headers.get('content-type') || ''
  const body = contentType.includes('application/json') ? await response.json() : null

  if (!response.ok) {
    const error = new Error(body?.message || 'Request failed')
    error.status = response.status
    throw error
  }

  return body
}

export const api = {
  me: () => request('/auth/me'),
  login: (payload) => request('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload)
  }),
  register: (payload) => request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload)
  }),
  logout: () => request('/auth/logout', { method: 'POST' }),
  concerts: () => request('/concerts'),
  book: (concertId, quantity) => request(`/concerts/${concertId}/bookings`, {
    method: 'POST',
    body: JSON.stringify({ quantity })
  }),
  myBookings: () => request('/bookings/my'),
  cancelBooking: (bookingId) => request(`/bookings/${bookingId}/cancel`, {
    method: 'POST'
  })
}
