import { CSVToArray, arrayToCSV } from '../utilities'
import { apiFetch, TIMEOUT } from '../api'
import { reportError } from './alerts'

export const PROTECT_DATASET = 'PROTECT_DATASET'
export const SAVE_PROTECTED_DATASET = 'SAVE_PROTECTED_DATASET'

const processConfiguration = (categories) => {
  const options = {}
  for (const categoryName in categories) {
    const category = categories[categoryName]
    for (const i in category) {
      const { id, description, value } = category[i]
      options[id] = {
        description,
        value,
        category: categoryName
      }
    }
  }
  return { options }
}

export function protectDataset (dataset, hasHeader, useCompound, direct, types, configuration) {
  return (dispatch, getState) => {
    if (needsProtection(getState())) {
      dispatch(startProtection())

      const fields = {}
      direct.forEach((v) => {
        const columnName = hasHeader ? dataset[0][v] : `Column ${v}`
        const type = types[columnName]
        if (type) {
          fields[columnName] = type
        } else {
          fields[columnName] = { name: '' }
        }
      })

      const body = new window.FormData()
      body.set('dataset', arrayToCSV(dataset))
      body.set('fields', JSON.stringify(fields))
      body.set('configuration', JSON.stringify(processConfiguration(configuration)))

      apiFetch(`api/feature/mask/${Boolean(hasHeader)}/${Boolean(useCompound)}`, {
        what: 'Unable to protect the dataset',
        as: 'text',
        method: 'POST',
        body,
        timeout: TIMEOUT.long
      }).then(
        data => {
          dispatch(saveProtectedData(CSVToArray(data.trim())))
        }
      ).catch(
        error => {
          dispatch(saveProtectedData(undefined))
          dispatch(reportError(error))
        }
      )
    } else {
      dispatch(saveProtectedData(getState().protect.data))
    }
  }
}

function needsProtection (state) {
  return !state.workflow.isFetching
}

export function saveProtectedData (data) {
  return {
    type: SAVE_PROTECTED_DATASET,
    data
  }
}

export function startProtection () {
  return {
    type: PROTECT_DATASET
  }
}
