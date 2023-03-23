import { SAVE_IDENTIFIERS, REQUEST_IDENTIFIERS } from '../actions/identifiers'

const reducer = (state = {}, action) => {
  switch (action.type) {
    case REQUEST_IDENTIFIERS:
      return Object.assign({}, state, {
        isFetching: true
      })
    case SAVE_IDENTIFIERS:
      return Object.assign({}, state, {
        data: action.identifiers,
        isFetching: false
      })
    default:
      return state
  }
}

export default reducer
