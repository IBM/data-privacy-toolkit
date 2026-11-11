// Single entry point for every call to the REST back-end: a timeout on each request and one kind of error
// (ApiError) whose message is safe to show to the user. Server response bodies are never put in the message.

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
 * `as` is the Response method used to read the body (`json`, `text`, `arrayBuffer`), `timeout` is in milliseconds;
 * everything else is passed on to `fetch`.
 *
 * Rejects with an ApiError when the server cannot be reached, does not answer in time, answers with an error
 * status, or sends a body that cannot be read as requested.
 */
export async function apiFetch (url, { what = 'Request failed', as = 'json', timeout = TIMEOUT.short, ...init } = {}) {
  const signal = AbortSignal.timeout(timeout)

  let response
  try {
    response = await window.fetch(url, { ...init, signal })
  } catch (error) {
    const reason = error?.name === 'TimeoutError' ? 'the server did not answer in time' : 'the server cannot be reached'
    throw new ApiError(`${what}: ${reason}`, { cause: error })
  }

  if (!response.ok) {
    throw new ApiError(`${what} (HTTP ${response.status})`, { status: response.status })
  }

  try {
    return await response[as]()
  } catch (error) {
    throw new ApiError(`${what}: unexpected response from the server`, { status: response.status, cause: error })
  }
}
