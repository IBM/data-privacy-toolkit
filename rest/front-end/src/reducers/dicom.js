import { NOTIFY_DICOM_MASKED, REQUEST_MASKING_DICOM, SAVE_DICOM_FILE, LOAD_DICOM_FILE, REQUEST_DICOM_IMAGE, SAVE_DICOM_IMAGE, REQUEST_DICOM_ATTRIBUTES, SAVE_DICOM_ATTRIBUTES, SAVE_MASKED_DICOM_ATTRIBUTES, SAVE_MASKED_DICOM_IMAGE } from '../actions/dicom'

const reducer = (state = { isFetching: 0 }, action) => {
  switch (action.type) {
    case LOAD_DICOM_FILE:
      return Object.assign({}, state, { isFetching: state.isFetching + 1 })
    case REQUEST_DICOM_IMAGE:
      return Object.assign({}, state, { isFetching: state.isFetching + 1 })
    case REQUEST_MASKING_DICOM:
      return Object.assign({}, state, { isFetching: state.isFetching + 1 })
    case REQUEST_DICOM_ATTRIBUTES:
      return Object.assign({}, state, { isFetching: state.isFetching + 1 })
    case SAVE_DICOM_FILE:
      return Object.assign({}, state, { isFetching: state.isFetching - 1, original: action.data })
    case SAVE_DICOM_IMAGE:
      return Object.assign({}, state, { isFetching: state.isFetching - 1, originalImage: action.data, maskedImage: undefined })
    case SAVE_DICOM_ATTRIBUTES:
      return Object.assign({}, state, { isFetching: state.isFetching - 1, originalMetadata: action.data, maskedMetadata: undefined })
    case SAVE_MASKED_DICOM_IMAGE:
      return Object.assign({}, state, { isFetching: state.isFetching - 1, maskedImage: action.data })
    case SAVE_MASKED_DICOM_ATTRIBUTES:
      return Object.assign({}, state, { isFetching: state.isFetching - 1, maskedMetadata: action.data })
    case NOTIFY_DICOM_MASKED:
      return Object.assign({}, state, { isFetching: state.isFetching - 1 })

    default:
      return state
  }
}

export default reducer
