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
    window.fetch('api/information/anonymizationProviders')
      .then(response => {
        if (response.ok) {
          return response.json()
        } else {
          throw new Error('Error retrieving anonymization providers the dataset')
        }
      }).then(json => dispatch(saveAnonymizationProviders(json)))
      .catch(error => {
        console.log(error)
        dispatch(saveAnonymizationProviders(undefined))
        window.alert('Unable to retrieve anonymization providers')
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
