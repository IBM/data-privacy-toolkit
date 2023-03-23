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

const headers = new window.Headers()
headers.append('Content-Type', 'application/octet-stream')

export function maskDicomFile (file) {
  return (dispatch, getState) => {
    if (beProcessed(getState())) {
      dispatch(requestMasking())
      window.fetch('api/feature/dicom/mask', {
        method: 'POST',
        headers,
        body: file
      }).then(response => {
        if (response.ok) {
          return response.arrayBuffer()
        }
        throw new Error(`Error masking: ${response.statusText}`)
      }).then(data => {
        dispatch(notifyDicomFileMasked(data))

        dispatch(requestImage())
        window.fetch('api/feature/dicom/image', {
          method: 'POST',
          headers,
          body: data
        }).then(response => {
          if (response.ok) {
            return response.text()
          }
          throw new Error(`Error processing image file ${response.statusText}`)
        }).then(image => {
          dispatch(storeMaskedDicomImage(`data:image/png;base64,${image}`))
        }).catch(error => {
          window.alert(error.message)
          dispatch(storeMaskedDicomImage(undefined))
        })

        dispatch(requestAttribute())
        window.fetch('api/feature/dicom/attributes', {
          method: 'POST',
          headers,
          body: data
        }).then(response => {
          if (response.ok) {
            return response.json()
          }
          throw new Error(`Error extracting attributes ${response.statusText}`)
        }).then(data => {
          dispatch(storeMaskedDicomAttributes(data))
        }).catch(error => {
          window.alert(error.message)
          dispatch(storeMaskedDicomAttributes(undefined))
        })
      }).catch(error => {
        window.alert(error.message)

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

        dispatch(requestImage())
        window.fetch('api/feature/dicom/image', {
          method: 'POST',
          headers,
          body: data
        }).then(response => {
          if (response.ok) {
            return response.text()
          }
          throw new Error(`Error processing image file ${response.statusText}`)
        }).then(image => {
          dispatch(storeOriginalDicomImage(`data:image/png;base64,${image}`))
        }).catch(error => {
          window.alert(error.message)
          dispatch(storeOriginalDicomImage(undefined))
        })

        dispatch(requestAttribute())
        window.fetch('api/feature/dicom/attributes', {
          method: 'POST',
          headers,
          body: data
        }).then(response => {
          if (response.ok) {
            return response.json()
          }
          throw new Error(`Error extracting attributes ${response.statusText}`)
        }).then(data => {
          dispatch(storeOriginalDicomAttributes(data))
        }).catch(error => {
          window.alert(error.message)
          dispatch(storeOriginalDicomAttributes(undefined))
        })
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
