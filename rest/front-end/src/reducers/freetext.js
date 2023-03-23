import { SAVE_ANNOTATION, REQUEST_ANNOTATION, SAVE_NOTE, REQUEST_NOTE, REQUEST_MASKING_CONFIGURATION, SAVE_MASKING_CONFIGURATION } from '../actions/freetext'

const reducer = (state = { isFetching: false }, action) => {
  switch (action.type) {
    case SAVE_ANNOTATION:
      return Object.assign({}, state, { isFetching: false, annotations: action.data })
    case SAVE_NOTE:
      return Object.assign({}, state, { isFetching: false, text: action.data })
    case SAVE_MASKING_CONFIGURATION:
      return Object.assign({}, state, { isFetching: false, configuration: action.data })
    case REQUEST_ANNOTATION:
    case REQUEST_NOTE:
    case REQUEST_MASKING_CONFIGURATION:
      return Object.assign({}, state, { isFetching: true })
    default:
      return state
  }
}

export default reducer
