import { ApiError } from '../api'

export const SHOW_ALERT = 'SHOW_ALERT'
export const DISMISS_ALERT = 'DISMISS_ALERT'

let lastId = 0

export function showAlert (message) {
  return {
    type: SHOW_ALERT,
    id: ++lastId,
    message
  }
}

export function dismissAlert (id) {
  return {
    type: DISMISS_ALERT,
    id
  }
}

/**
 * Tells the user that an operation failed. ApiError messages are written for the user; anything else is a defect
 * in the application, so only a generic message is shown and the details go to the console.
 */
export function reportError (error) {
  return dispatch => {
    if (error instanceof ApiError) {
      dispatch(showAlert(error.message))
    } else {
      console.error(error)
      dispatch(showAlert('Unexpected error. Please try again.'))
    }
  }
}
