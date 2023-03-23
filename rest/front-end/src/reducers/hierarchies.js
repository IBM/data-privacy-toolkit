import { REQUEST_HIERARCHY, SAVE_HIERARCHY } from '../actions/hierarchies'

const reducer = (state = {}, action) => {
  switch (action.type) {
    case REQUEST_HIERARCHY:
      return Object.assign({}, state, {isFetching: true})
    case SAVE_HIERARCHY:
      if (action.data && action.hierarchy) {
        const data = Object.assign({}, state.data)
        data[action.hierarchy] = action.data
        return Object.assign({}, state, {isFetching: false, data})
      } else {
        return Object.assign({}, state, {isFetching: false})
      }
    default:
      return state
  }
}

export default reducer
