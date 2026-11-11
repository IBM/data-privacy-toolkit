import { describe, it, expect, vi, afterEach } from 'vitest'

import { apiFetch, ApiError, TIMEOUT } from './api'

const respond = (status, body) => new Response(body, { status })

afterEach(() => {
  vi.restoreAllMocks()
})

describe('apiFetch', () => {
  it('returns the parsed JSON body by default', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(respond(200, '{"a":1}'))

    expect(await apiFetch('api/x')).toEqual({ a: 1 })
  })

  it('can read the body as text', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(respond(200, 'a,b'))

    expect(await apiFetch('api/x', { as: 'text' })).toBe('a,b')
  })

  it('passes the request options on, with a timeout signal', async () => {
    const fetch = vi.spyOn(window, 'fetch').mockResolvedValue(respond(200, '{}'))

    await apiFetch('api/x', { method: 'POST', body: 'data', what: 'Doing x', timeout: 5000 })

    const [url, init] = fetch.mock.calls[0]
    expect(url).toBe('api/x')
    expect(init.method).toBe('POST')
    expect(init.body).toBe('data')
    expect(init.signal).toBeInstanceOf(AbortSignal)
    expect(init).not.toHaveProperty('what')
    expect(init).not.toHaveProperty('timeout')
  })

  it('rejects with the status but without the server message when the status is an error', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(respond(500, 'java.lang.NullPointerException at secret.Class'))

    const error = await apiFetch('api/x', { what: 'Unable to do x' }).catch(e => e)

    expect(error).toBeInstanceOf(ApiError)
    expect(error.status).toBe(500)
    expect(error.message).toBe('Unable to do x (HTTP 500)')
  })

  it('rejects when the server cannot be reached', async () => {
    vi.spyOn(window, 'fetch').mockRejectedValue(new TypeError('Failed to fetch'))

    const error = await apiFetch('api/x', { what: 'Unable to do x' }).catch(e => e)

    expect(error).toBeInstanceOf(ApiError)
    expect(error.message).toBe('Unable to do x: the server cannot be reached')
    expect(error.cause).toBeInstanceOf(TypeError)
  })

  it('gives up when the server does not answer within the timeout', async () => {
    vi.spyOn(window, 'fetch').mockImplementation((url, { signal }) => new Promise((resolve, reject) => {
      signal.addEventListener('abort', () => reject(signal.reason))
    }))

    const error = await apiFetch('api/x', { what: 'Unable to do x', timeout: 20 }).catch(e => e)

    expect(error).toBeInstanceOf(ApiError)
    expect(error.message).toBe('Unable to do x: the server did not answer in time')
  })

  it('rejects when the body cannot be read as requested', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(respond(200, '<html>not json</html>'))

    const error = await apiFetch('api/x', { what: 'Unable to do x' }).catch(e => e)

    expect(error).toBeInstanceOf(ApiError)
    expect(error.message).toBe('Unable to do x: unexpected response from the server')
  })

  it('allows more time to the operations that process data sets', () => {
    expect(TIMEOUT.long).toBeGreaterThan(TIMEOUT.short)
  })
})
