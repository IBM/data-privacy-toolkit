import { REQUEST_INFORMATION_LOSS_METRICS, SAVE_INFORMATION_LOSS_METRICS } from '../actions/informationLossMetrics'

const reducer = (state = {}, action) => {
  switch (action.type) {
    case REQUEST_INFORMATION_LOSS_METRICS:
      return Object.assign({}, state, {
        isFetching: true
      })
    case SAVE_INFORMATION_LOSS_METRICS:
      return Object.assign({}, state, {
        data: action.data,
        isFetching: false
      })
    default:
      return state
  }
}

export default reducer
