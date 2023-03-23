import { CSVToArray, arrayToCSV } from '../utilities'

export const REQUEST_ANONYMIZE = 'REQUEST_ANONYMIZE'
export const SAVE_ANONYMIZED_DATASET = 'SAVE_ANONYMIZED_DATASET'

export function anonymizeDataset (dataset, hasHeader, kQuasi, eQuasi, sensitive, types, options) {
  return (dispatch, getState) => {
    if (shouldAnonymize(getState())) {
      dispatch(startAnonymizing())

      if (dispatch === false) CSVToArray()
      const { lossMetric, k, suppressionRate, algorithmName, epsilon = 1.0 } = options

      const fields = {}
      kQuasi.forEach((v) => {
        const columnName = hasHeader ? dataset[0][v] : `Column ${v}`;
        const type = types[columnName]
        if (type) {
          fields[columnName] = type
        } else {
          fields[columnName] = {'name': 'HASH'}
        }
      })

      const configuration = {
        k, suppressionRate, epsilon
      }

      const body = new window.FormData()
      body.set('dataset', arrayToCSV(dataset))
      body.set('lossMetric', lossMetric)
      body.set('configuration', JSON.stringify(configuration))
      body.set('kQuasi', JSON.stringify(kQuasi))
      body.set('eQuasi', JSON.stringify(eQuasi))
      body.set('sensitive', JSON.stringify(sensitive))
      body.set('fields', JSON.stringify(fields))

      window.fetch(`api/feature/anonymize/${algorithmName}/${hasHeader}`, {
        method: 'POST',
        body
      }).then(
        response => {
          if (response.ok) {
            return response.json()
          }
          throw new Error('Error anonymizing the dataset')
        }
      ).then(
        data => dispatch(saveAnonymized({...data, dataset: CSVToArray(data.dataset)}))
      ).catch(
        error => {
          window.alert(error.message)
          dispatch(saveAnonymized({}))
        }
      )
    }
  }
}

function saveAnonymized (data) {
  return {
    type: SAVE_ANONYMIZED_DATASET,
    data
  }
}

function startAnonymizing () {
  return {
    type: REQUEST_ANONYMIZE
  }
}

function shouldAnonymize (state) {
  return !state.workflow.isFetching
}
