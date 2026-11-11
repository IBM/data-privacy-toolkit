import { apiFetch } from '../api'
import { reportError } from './alerts'

export const REQUEST_IDENTIFIERS = 'REQUEST_IDENTIFIER'
export const SAVE_IDENTIFIERS = 'SAVE_IDENTIFIERS'

export function getIdentifiersIfNeeded () {
  return (dispatch, getState) => {
    if (shouldFetchIdentifiers(getState())) {
      return dispatch(fetchIdentifiers())
    }
  }
}

function requestIdentifiers () {
  return {
    type: REQUEST_IDENTIFIERS
  }
}

function fetchIdentifiers () {
  return dispatch => {
    dispatch(requestIdentifiers())
    apiFetch('api/information/identifiers', { what: 'Unable to retrieve the identifiers' })
      .then(json => dispatch(saveIdentifiers(json)))
      .catch(error => {
        dispatch(saveIdentifiers(undefined))
        dispatch(reportError(error))
      })
  }
}

function saveIdentifiers (identifiers) {
  return {
    type: SAVE_IDENTIFIERS,
    identifiers
  }
}

function shouldFetchIdentifiers (state) {
  if (state.identifiers.isFetching) {
    return false
  } else if (!state.identifiers.data) {
    return true
  }
  return false
}
