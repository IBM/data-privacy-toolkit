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
    window.fetch('api/information/maskingProviders').then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Unable to retrive the masking providers')
      }
    }).then(json => dispatch(saveMaskingProviders(json)))
      .catch(error => {
        console.log(error)
        dispatch(saveMaskingProviders(undefined))
        window.alert(error.message)
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
