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
    window.fetch('api/information/identifiers').then(response => {
      if (response.ok) {
        return response.json()
      }
      throw new Error('Unable to retrieve identifiers')
    }).then(json => dispatch(saveIdentifiers(json))).catch(
      error => {
        console.log(error.message)
        window.alert(error.message)
        dispatch(saveIdentifiers(undefined))
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
