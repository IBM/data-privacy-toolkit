import { apiFetch, TIMEOUT } from '../api'
import { reportError } from './alerts'

export const IDENTIFY_DATASET = 'IDENTIFY_DATASET'
export const SAVE_IDENTIFICATION = 'SAVE_IDENTIFICATION'

export function identifyDataset (dataset, hasHeader, sampleSize) {
  return (dispatch, getState) => {
    if (needsIdentification(getState())) {
      dispatch(startIdentification())

      sampleSize = sampleSize || 1000

      apiFetch(`api/feature/identify/${Boolean(hasHeader)}?sampleSize=${sampleSize}`, {
        what: 'Unable to identify the column types',
        method: 'POST',
        headers: { 'Content-Type': 'text/csv' },
        body: dataset,
        timeout: TIMEOUT.long
      }).then(data => dispatch(saveIdentifiedData(data)))
        .catch(error => {
          dispatch(saveIdentifiedData(undefined))
          dispatch(reportError(error))
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
    dispatch(saveIdentifiedData(Object.assign({}, getState().workflow.identifiedTypes, { [columnName]: provider })))
  }
}
