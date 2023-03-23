export const REQUEST_EXPLORATION = 'REQUEST_EXPLORATION'
export const SAVE_EXPLORATION = 'SAVE_EXPLORATION'
export const SAVE_LINKING = 'SAVE_LINKING'
export const SAVE_CONFIGURATION = 'SAVE_CONFIGURATION'

export function saveLinking (linkingData) {
  return {
    type: SAVE_LINKING,
    linkingData
  }
}

export function saveConfiguration (k, suppression) {
  return {
    type: SAVE_CONFIGURATION,
    k,
    suppression
  }
}

export function exploreIfNeeded (hasHeader,
  k,
  suppression,
  fields,
  vulnerabilities,
  sensitive,
  linkingData,
  data) {
  return (dispatch, getState) => {
    if (shouldExplore(getState())) {
      return dispatch(explore(hasHeader,
        k,
        suppression,
        fields,
        vulnerabilities,
        sensitive,
        linkingData,
        data))
    }
  }
}

function explore (hasHeader,
  k,
  suppression,
  fields,
  vulnerabilities,
  sensitive,
  linkingData,
  data) {
  return dispatch => {
    dispatch(requestExploration())

    // network magic
    const body = new window.FormData()
    body.append('fields', JSON.stringify(fields))
    body.append('vulnerabilities', JSON.stringify(vulnerabilities.filter(i => i !== null)))
    body.append('sensitive', JSON.stringify(sensitive.filter(i => i !== null)))
    body.append('linkingAttributes', JSON.stringify(linkingData))
    body.append('data', data)

    const url = `api/feature/explore/${Boolean(hasHeader)}?` +
      `minK=${k.min}&maxK=${k.max}&kInterval=${k.step}&` +
      `minSuppression=${suppression.min}&maxSuppression=${suppression.max}&suppressionInterval=${suppression.step}`

    window.fetch(
      url,
      {
        method: 'POST',
        body
      }).then(response => {
      if (response.ok) {
        return response.json()
      } else throw new Error('Unable to explore correctly')
    }).then(data => dispatch(saveExploration(data)))
      .catch(error => {
        window.alert(error.message)
        dispatch(saveExploration())
      })
  }
}

function requestExploration () {
  return {
    type: REQUEST_EXPLORATION
  }
}

function saveExploration (data) {
  return {
    type: SAVE_EXPLORATION,
    data
  }
}

function shouldExplore (state) {
  const configuration = state.workflow

  return !configuration.isFetching
}
