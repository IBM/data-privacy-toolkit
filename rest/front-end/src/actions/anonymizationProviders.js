import { apiFetch } from '../api'
import { reportError } from './alerts'

export const REQUEST_ANONYMIZATION_PROVIDERS = 'REQUEST_ANONYMIZATION_PROVIDERS'
export const SAVE_ANONYMIZATION_PROVIDERS = 'SAVE_ANONYMIZATION_PROVIDERS'

export function getAnonymizationProvidersIfNeeded () {
  return (dispatch, getState) => {
    if (shouldFetchAnonymizationProviders(getState())) {
      return dispatch(fetchAnonymizationProviders())
    }
  }
}

function requestAnonymizationProviders () {
  return {
    type: REQUEST_ANONYMIZATION_PROVIDERS
  }
}

function fetchAnonymizationProviders () {
  return dispatch => {
    dispatch(requestAnonymizationProviders())
    apiFetch('api/information/anonymizationProviders', { what: 'Unable to retrieve the anonymization providers' })
      .then(json => dispatch(saveAnonymizationProviders(json)))
      .catch(error => {
        dispatch(saveAnonymizationProviders(undefined))
        dispatch(reportError(error))
      })
  }
}

function saveAnonymizationProviders (anonymizationProviders) {
  return {
    type: SAVE_ANONYMIZATION_PROVIDERS,
    anonymizationProviders
  }
}

function shouldFetchAnonymizationProviders (state) {
  if (state.anonymizationProviders.isFetching) {
    return false
  } else if (!state.anonymizationProviders.data) {
    return true
  }
  return false
}
