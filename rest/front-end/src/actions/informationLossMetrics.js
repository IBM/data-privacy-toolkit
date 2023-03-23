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

    window.fetch('api/information/informationLoss').then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Unable to retrieve information loss metrics')
      }
    }).then(data => dispatch(saveInformationLossMetrics(data)))
      .catch(error => {
        console.log(error)
        dispatch(saveInformationLossMetrics(undefined))
        window.alert(error.message)
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
