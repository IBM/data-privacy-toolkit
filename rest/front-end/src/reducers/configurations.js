import { SAVE_MASKING_CONFIGURATION, REQUEST_MASKING_CONFIGURATION,
         SAVE_DIFF_CONFIGURATION, REQUEST_DIFF_CONFIGURATION,
         REQUEST_ANONYMIZATION_PROVIDER_CONFIGURATION, SAVE_ANONYMIZATION_PROVIDER_CONFIGURATION } from '../actions/configurations'

const reducer = (state = {}, action) => {
  switch (action.type) {
    case REQUEST_ANONYMIZATION_PROVIDER_CONFIGURATION:
      return Object.assign({}, state, {
        anonymizationProviders: Object.assign({}, state.anonymizationProviders, {
          [action.algorithmName]: {
            isFetching: true
          }
        })
      })
    case SAVE_ANONYMIZATION_PROVIDER_CONFIGURATION:
      return Object.assign({}, state, {
        anonymizationProviders: Object.assign({}, state.anonymizationProviders, {
          [action.algorithmName]: {
            isFetching: false,
            configuration: action.configuration
          }
        })
      })
    case SAVE_MASKING_CONFIGURATION:
      return Object.assign({}, state, {
        maskingConfiguration: action.maskingConfiguration,
        isFetching: false
      })
    case REQUEST_DIFF_CONFIGURATION:
    case REQUEST_MASKING_CONFIGURATION:
      return Object.assign({}, state, {
        isFetching: true
      })
    case SAVE_DIFF_CONFIGURATION:
      return Object.assign({}, state, {
        epsilon: action.value
      })
    default:
      return state
  }
}

export default reducer
