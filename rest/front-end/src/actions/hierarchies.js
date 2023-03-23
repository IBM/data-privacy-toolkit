export const REQUEST_HIERARCHY = 'REQUEST_HIERARCHY'
export const SAVE_HIERARCHY = 'SAVE_HIERARCHY'

export function getHierarchyIfNeeded (hierarchyName) {
  return (dispatch, getState) => {
    if (shouldFetchHierarchy(getState(), hierarchyName)) {
      return dispatch(fetchHierarchy(hierarchyName))
    }
  }
}

function shouldFetchHierarchy (state, hierarchy) {
  if (state.isFetching) return false
  return undefined === state.data || undefined === state.data[hierarchy]
}

function fetchHierarchy (hierarchy) {
  return dispatch => {
    dispatch(requestHierarchy())

    window.fetch(`api/information/hierarchy/${hierarchy}`).then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Unable to retrieve hierarchy ' + hierarchy)
      }
    }).then(data => dispatch(saveHierarchy(data, hierarchy)))
      .catch(error => {
        console.log(error)
        dispatch(saveHierarchy(undefined, undefined))
        window.alert(error.message)
      })
  }
}

function requestHierarchy () {
  return {
    type: REQUEST_HIERARCHY
  }
}

function saveHierarchy (data, hierarchy) {
  return {
    type: SAVE_HIERARCHY,
    data,
    hierarchy
  }
}
