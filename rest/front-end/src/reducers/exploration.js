import { SAVE_LINKING, REQUEST_EXPLORATION, SAVE_EXPLORATION } from '../actions/exploration'
import { CLEAR_STATE } from '../actions/common'

const reducer = (state = {
  isFetching: false,
  data: {},
  linkingData: []
}, action) => {
  switch (action.type) {
    case REQUEST_EXPLORATION:
      return Object.assign({}, state, {isFetching: true})
    case SAVE_EXPLORATION:
      return Object.assign({}, state, {isFetching: false, data: action.data})
    case SAVE_LINKING:
      return Object.assign({}, state, {linkingData: action.linkingData})
    case CLEAR_STATE:
      return {}
    default:
      return state
  }
}

export default reducer
