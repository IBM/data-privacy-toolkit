import { apiFetch } from '../api'
import { reportError } from './alerts'

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

    apiFetch(`api/information/hierarchy/${encodeURIComponent(hierarchy)}`, { what: `Unable to retrieve the hierarchy ${hierarchy}` })
      .then(data => dispatch(saveHierarchy(data, hierarchy)))
      .catch(error => {
        dispatch(saveHierarchy(undefined, undefined))
        dispatch(reportError(error))
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
