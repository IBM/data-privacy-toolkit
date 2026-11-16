import { apiFetch, TIMEOUT } from '../api'
import { reportError } from './alerts'

export const LOAD_DICOM_FILE = 'LOAD_DICOM_FILE'
export const SAVE_DICOM_FILE = 'SAVE_DICOM_FILE'

export const REQUEST_DICOM_IMAGE = 'REQUEST_DICOM_IMAGE'
export const SAVE_DICOM_IMAGE = 'SAVE_DICOM_IMAGE'

export const REQUEST_DICOM_ATTRIBUTES = 'REQUEST_DICOM_ATTRIBUTES'
export const SAVE_DICOM_ATTRIBUTES = 'SAVE_DICOM_ATTRIBUTES'

export const REQUEST_MASKING_DICOM = 'REQUEST_MASKING_DICOM'
export const SAVE_MASKED_DICOM_IMAGE = 'SAVE_MASKED_DICOM_IMAGE'
export const SAVE_MASKED_DICOM_ATTRIBUTES = 'SAVE_MASKED_DICOM_ATTRIBUTES'

export const NOTIFY_DICOM_MASKED = 'NOTIFY_DICOM_MASKED'

const OCTET_STREAM = { 'Content-Type': 'application/octet-stream' }

const post = (url, what, as, body, timeout = TIMEOUT.short) => apiFetch(url, { what, as, method: 'POST', headers: OCTET_STREAM, body, timeout })

function loadImage (dispatch, data, store) {
  dispatch(requestImage())
  post('api/feature/dicom/image', 'Unable to process the image file', 'text', data)
    .then(image => dispatch(store(`data:image/png;base64,${image}`)))
    .catch(error => {
      dispatch(reportError(error))
      dispatch(store(undefined))
    })
}

function loadAttributes (dispatch, data, store) {
  dispatch(requestAttribute())
  post('api/feature/dicom/attributes', 'Unable to extract the attributes', 'json', data)
    .then(attributes => dispatch(store(attributes)))
    .catch(error => {
      dispatch(reportError(error))
      dispatch(store(undefined))
    })
}

export function maskDicomFile (file) {
  return (dispatch, getState) => {
    if (beProcessed(getState())) {
      dispatch(requestMasking())
      post('api/feature/dicom/mask', 'Unable to mask the file', 'arrayBuffer', file, TIMEOUT.long)
        .then(data => {
          dispatch(notifyDicomFileMasked(data))

          loadImage(dispatch, data, storeMaskedDicomImage)
          loadAttributes(dispatch, data, storeMaskedDicomAttributes)
        }).catch(error => {
          dispatch(reportError(error))
          dispatch(notifyDicomFileMasked(undefined))
        })
    }
  }
}

export function loadDicomFile (file) {
  return (dispatch, getState) => {
    if (beProcessed(getState())) {
      dispatch(notifyFileLoading())

      const reader = new window.FileReader()
      reader.onload = event => {
        const data = new Uint8Array(event.target.result)
        dispatch(storeOriginalDicomFile(data))

        loadImage(dispatch, data, storeOriginalDicomImage)
        loadAttributes(dispatch, data, storeOriginalDicomAttributes)
      }
      reader.readAsArrayBuffer(file)
    }
  }
}

const storeMaskedDicomImage = data => {
  return {
    type: SAVE_MASKED_DICOM_IMAGE,
    data
  }
}

const storeMaskedDicomAttributes = data => {
  return {
    type: SAVE_MASKED_DICOM_ATTRIBUTES,
    data
  }
}

const notifyDicomFileMasked = () => {
  return {
    type: NOTIFY_DICOM_MASKED
  }
}

const requestMasking = () => {
  return {
    type: REQUEST_MASKING_DICOM
  }
}

const storeOriginalDicomImage = data => {
  return {
    type: SAVE_DICOM_IMAGE,
    data
  }
}

const storeOriginalDicomAttributes = data => {
  return {
    type: SAVE_DICOM_ATTRIBUTES,
    data
  }
}

const requestImage = () => {
  return {
    type: REQUEST_DICOM_IMAGE
  }
}

const requestAttribute = () => {
  return {
    type: REQUEST_DICOM_ATTRIBUTES
  }
}

const storeOriginalDicomFile = (data) => {
  return {
    type: SAVE_DICOM_FILE,
    data
  }
}

const notifyFileLoading = () => {
  return {
    type: LOAD_DICOM_FILE
  }
}

const beProcessed = state => {
  const dicomState = state.dicom
  return !dicomState.isFetching
}
