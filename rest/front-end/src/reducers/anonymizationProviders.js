import { SAVE_ANONYMIZATION_PROVIDERS, REQUEST_ANONYMIZATION_PROVIDERS } from '../actions/anonymizationProviders'

const reducer = (state = {}, action) => {
  switch (action.type) {
    case REQUEST_ANONYMIZATION_PROVIDERS:
      return Object.assign({}, state, {
        isFetching: true
      })
    case SAVE_ANONYMIZATION_PROVIDERS:
      return Object.assign({}, state, {
        data: action.anonymizationProviders,
        isFetching: false
      })
    default:
      return state
  }
}

export default reducer
