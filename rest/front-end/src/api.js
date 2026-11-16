// Single entry point for every call to the REST back-end: a timeout on each request and one kind of error
// (ApiError) whose message is safe to show to the user. Server response bodies are not put in the message, unless
// the caller asks for it with `explain` (only for endpoints that answer with a fixed, user-oriented `error` text).

// Milliseconds. `short` is for lookups, `long` for the operations that process a whole data set.
export const TIMEOUT = Object.freeze({ short: 30000, long: 600000 })

export class ApiError extends Error {
  constructor (message, { status, cause } = {}) {
    super(message, { cause })
    this.name = 'ApiError'
    this.status = status
  }
}

/**
 * Calls `url` and returns the parsed body.
 *
 * Options: `what` describes the operation for the error message ("Unable to retrieve the identifiers"),
 * `as` is the Response method used to read the body (`json`, `text`, `arrayBuffer`), `timeout` is in milliseconds,
 * `explain` adds the `error` text of a JSON error body to the message; everything else is passed on to `fetch`.
 *
 * Rejects with an ApiError when the server cannot be reached, does not answer in time, answers with an error
 * status, or sends a body that cannot be read as requested.
 */
export async function apiFetch (url, { what = 'Request failed', as = 'json', timeout = TIMEOUT.short, explain = false, ...init } = {}) {
  const signal = AbortSignal.timeout(timeout)

  let response
  try {
    response = await window.fetch(url, { ...init, signal })
  } catch (error) {
    const reason = error?.name === 'TimeoutError' ? 'the server did not answer in time' : 'the server cannot be reached'
    throw new ApiError(`${what}: ${reason}`, { cause: error })
  }

  if (!response.ok) {
    const reason = explain ? await explanation(response) : undefined
    throw new ApiError(reason ? `${what}: ${reason}` : `${what} (HTTP ${response.status})`, { status: response.status })
  }

  try {
    return await response[as]()
  } catch (error) {
    throw new ApiError(`${what}: unexpected response from the server`, { status: response.status, cause: error })
  }
}

async function explanation (response) {
  try {
    const { error } = await response.json()
    return typeof error === 'string' && error.length <= 200 ? error : undefined
  } catch {
    return undefined
  }
}
