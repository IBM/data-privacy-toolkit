import { apiFetch, TIMEOUT } from '../api'
import { reportError } from './alerts'

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
    // JSON.stringify(new Set([1])) is "{}", so make sure to send an array
    body.append('linkingAttributes', JSON.stringify(Array.from(linkingData)))
    body.append('data', data)

    const url = `api/feature/explore/${Boolean(hasHeader)}?` +
      `minK=${k.min}&maxK=${k.max}&kInterval=${k.step}&` +
      `minSuppression=${suppression.min}&maxSuppression=${suppression.max}&suppressionInterval=${suppression.step}`

    apiFetch(url, {
      what: 'Unable to explore the risk and utility',
      method: 'POST',
      body,
      timeout: TIMEOUT.long,
      explain: true
    }).then(data => dispatch(saveExploration(data)))
      .catch(error => {
        dispatch(reportError(error))
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

// The limits the back-end enforces on the values to explore (Explore.java), so that mistakes are reported before the
// request is sent.
const MAX_COMBINATIONS = 100

const count = (min, max, step) => Math.floor((max - min) / step) + 1

/** Why the values cannot be explored, or undefined if they can. */
export function explorationProblem (k, suppression) {
  const { min: minK, max: maxK, step: stepK } = k
  const { min: minS, max: maxS, step: stepS } = suppression

  if (![minK, maxK, stepK, minS, maxS, stepS].every(Number.isFinite)) return 'Fill in all the values.'
  if (![minK, maxK, stepK].every(Number.isInteger)) return 'The values of k must be integers.'
  if (minK < 2) return 'The minimum k must be at least 2.'
  if (maxK < minK) return 'The maximum k must not be smaller than the minimum.'
  if (stepK < 1) return 'The k step must be at least 1.'
  if (minS < 0 || maxS > 100) return 'The suppression must be between 0 and 100.'
  if (maxS < minS) return 'The maximum suppression must not be smaller than the minimum.'
  if (!(stepS > 0)) return 'The suppression step must be greater than 0.'
  if (count(minK, maxK, stepK) * count(minS, maxS, stepS) > MAX_COMBINATIONS) {
    return `Too many combinations of k and suppression: at most ${MAX_COMBINATIONS}. Increase a step or narrow a range.`
  }
}

function shouldExplore (state) {
  const configuration = state.workflow

  return !configuration.isFetching
}
