import { apiFetch } from '../api'
import { reportError } from './alerts'

export const REQUEST_INFORMATION_LOSS_METRICS = 'REQUEST_INFORMATION_LOSS_METRICS'
export const SAVE_INFORMATION_LOSS_METRICS = 'SAVE_INFORMATION_LOSS_METRICS'

export function getInformationLossMetricsIfNeeded () {
  return (dispatch, getState) => {
    if (shouldFetchInformationLossMetrics(getState())) {
      return dispatch(fetchInformationLossMetrics())
    }
  }
}

function shouldFetchInformationLossMetrics (state) {
  if (state.isFetching) return false
  return undefined === state.data
}

function fetchInformationLossMetrics () {
  return dispatch => {
    dispatch(requestInformationLossMetrics())

    apiFetch('api/information/informationLoss', { what: 'Unable to retrieve the information loss metrics' })
      .then(data => dispatch(saveInformationLossMetrics(data)))
      .catch(error => {
        dispatch(saveInformationLossMetrics(undefined))
        dispatch(reportError(error))
      })
  }
}

function requestInformationLossMetrics () {
  return {
    type: REQUEST_INFORMATION_LOSS_METRICS
  }
}

function saveInformationLossMetrics (data) {
  return {
    type: SAVE_INFORMATION_LOSS_METRICS,
    data
  }
}
