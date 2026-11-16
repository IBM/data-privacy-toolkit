import { DatasetToArray } from '../utilities'

import { apiFetch } from '../api'
import { notifyClearState } from './common'
import { reportError } from './alerts'

export const LOAD_DATASET = 'LOAD_DATASET'
export const SAVE_DATASET = 'SAVE_DATASET'

export function notifyLoading () {
  return {
    type: LOAD_DATASET
  }
}

export function notifySave (data, hasHeader = false) {
  return {
    type: SAVE_DATASET,
    data,
    hasHeader
  }
}

export function loadLocal (name) {
  return dispatch => {
    dispatch(notifyClearState())
    dispatch(notifyLoading())
    apiFetch(`datasets/${encodeURIComponent(name)}`, { what: `Unable to load ${name}`, as: 'text' }).then(
      data => dispatch(notifySave(DatasetToArray(name, data.trim())))
    ).catch(error => {
      dispatch(notifySave(undefined))
      dispatch(reportError(error))
    })
  }
}

export function loadRemote (file, hasHeader) {
  return dispatch => {
    dispatch(notifyClearState())
    dispatch(notifyLoading())
    const reader = new window.FileReader()
    reader.onload = event => dispatch(notifySave(DatasetToArray(file.name, event.target.result.trim(), hasHeader), hasHeader))
    reader.readAsText(file)
  }
}
