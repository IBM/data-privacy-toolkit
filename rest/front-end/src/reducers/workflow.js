import { SAVE_ANONYMIZED_DATASET, REQUEST_ANONYMIZE } from '../actions/anonymize'
import { LOAD_DATASET, SAVE_DATASET } from '../actions/loading'
import { IDENTIFY_DATASET, SAVE_IDENTIFICATION } from '../actions/identify'
import { ANALYZE_DATASET, SAVE_RISK_IDENTIFICATION } from '../actions/risk'
import { PROTECT_DATASET, SAVE_PROTECTED_DATASET } from '../actions/protect'
import { SAVE_LINKING, REQUEST_EXPLORATION, SAVE_EXPLORATION, SAVE_CONFIGURATION } from '../actions/exploration'
import { CLEAR_STATE } from '../actions/common'

const INITIAL_STATE = {
  isFetching: false
}

const reducer = (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case LOAD_DATASET:
    case IDENTIFY_DATASET:
    case REQUEST_EXPLORATION:
    case ANALYZE_DATASET:
    case PROTECT_DATASET:
    case REQUEST_ANONYMIZE:
      return Object.assign({}, state, { isFetching: true })

    case SAVE_DATASET:
      return Object.assign({}, state, { isFetching: false, dataset: action.data, hasHeader: action.hasHeader })

    case SAVE_IDENTIFICATION:
      return Object.assign({}, state, {
        isFetching: false,
        identifiedTypes: action.data
      })

    case SAVE_EXPLORATION:
      return Object.assign({}, state, {isFetching: false, explorationReport: action.data})
    case SAVE_LINKING:
      return Object.assign({}, state, {linkingData: action.linkingData})
    case SAVE_CONFIGURATION:
      return Object.assign({}, state, {
        k: action.k,
        suppression: action.suppression
      })

    case SAVE_RISK_IDENTIFICATION:
      return Object.assign({}, state, {
        isFetching: false,
        direct: action.direct,
        kQuasi: action.kQuasi,
        eQuasi: action.eQuasi,
        sensitive: action.sensitive,
        k: action.k
      })

    case SAVE_PROTECTED_DATASET:
      return Object.assign({}, state, {
        isFetching: false,
        protectedDataset: action.data
      })

    case SAVE_ANONYMIZED_DATASET:
      const { dataset, columnInformation, informationLossResult, suppression } = action.data
      return Object.assign({}, state, {
        isFetching: false,
        anonymizedDataset: dataset,
        columnInformation,
        informationLossResult,
        suppression
      })

    case CLEAR_STATE:
      return INITIAL_STATE

    default:
      return state
  }
}

export default reducer
