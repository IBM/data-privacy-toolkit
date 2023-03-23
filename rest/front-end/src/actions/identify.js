export const IDENTIFY_DATASET = 'IDENTIFY_DATASET'
export const SAVE_IDENTIFICATION = 'SAVE_IDENTIFICATION'

export function identifyDataset (dataset, hasHeader, sampleSize) {
  return (dispatch, getState) => {
    if (needsIdentification(getState())) {
      dispatch(startIdentification())

      const headers = new window.Headers()
      headers.set('Content-Type', 'text/csv')

      sampleSize = sampleSize || 1000

      window.fetch(`api/feature/identify/${Boolean(hasHeader)}?sampleSize=${sampleSize}`, {
        method: 'POST',
        headers,
        body: dataset
      }).then(response => {
        if (response.ok) {
          return response.json()
        }
        throw new Error('Error identifying dataset')
      }).then(data => dispatch(saveIdentifiedData(data)))
        .catch(error => {
          dispatch(saveIdentifiedData(undefined))
          window.alert(error.message)
        })
    }
  }
}

function needsIdentification (state) {
  return !state.workflow.isFetching
}

export function saveIdentifiedData (data) {
  return {
    type: SAVE_IDENTIFICATION,
    data
  }
}

export function startIdentification () {
  return {
    type: IDENTIFY_DATASET
  }
}

export function updateColumnValue (columnName, provider) {
  return (dispatch, getState) => {
    dispatch(saveIdentifiedData(Object.assign({}, getState().workflow.identifiedTypes, {[columnName]: provider})))
  }
}
