import { DatasetToArray } from '../utilities'

import { notifyClearState } from './common'

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
    window.fetch(`datasets/${name}`).then(
      response => response.text()
    ).then(
      data => dispatch(notifySave(DatasetToArray(name, data.trim())))
    )
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
