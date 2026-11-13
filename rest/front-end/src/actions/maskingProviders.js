import { apiFetch } from '../api'
import { reportError } from './alerts'

export const REQUEST_MASKING_PROVIDERS = 'REQUEST_MASKING_PROVIDERS'
export const SAVE_MASKING_PROVIDERS = 'SAVE_MASKING_PROVIDERS'

export function getMaskingProvidersIfNeeded () {
  return (dispatch, getState) => {
    if (shouldFetchMaskingProviders(getState())) {
      return dispatch(fetchMaskingProviders())
    }
  }
}

function requestMaskingProviders () {
  return {
    type: REQUEST_MASKING_PROVIDERS
  }
}

function fetchMaskingProviders () {
  return dispatch => {
    dispatch(requestMaskingProviders())
    apiFetch('api/information/maskingProviders', { what: 'Unable to retrieve the masking providers' })
      .then(json => dispatch(saveMaskingProviders(json)))
      .catch(error => {
        dispatch(saveMaskingProviders(undefined))
        dispatch(reportError(error))
      })
  }
}

function saveMaskingProviders (maskingProviders) {
  return {
    type: SAVE_MASKING_PROVIDERS,
    maskingProviders
  }
}

function shouldFetchMaskingProviders (state) {
  if (state.maskingProviders.isFetching) {
    return false
  }
  return !state.maskingProviders.data
}
