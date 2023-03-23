import { SAVE_MASKING_PROVIDERS, REQUEST_MASKING_PROVIDERS } from '../actions/maskingProviders'

const reducer = (state = {}, action) => {
  switch (action.type) {
    case REQUEST_MASKING_PROVIDERS:
      return Object.assign({}, state, {
        isFetching: true
      })
    case SAVE_MASKING_PROVIDERS:
      return Object.assign({}, state, {
        data: action.maskingProviders,
        isFetching: false
      })
    default:
      return state
  }
}

export default reducer
