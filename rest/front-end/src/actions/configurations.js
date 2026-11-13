import { apiFetch } from '../api'
import { reportError } from './alerts'

export const SAVE_MASKING_CONFIGURATION = 'SAVE_MASKING_CONFIGURATION'
export const REQUEST_MASKING_CONFIGURATION = 'REQUEST_MASKING_CONFIGURATION'

export function getMaskingConfigurationIfNeeded () {
  return (dispatch, getState) => {
    if (shouldFetchMaskingConfiguration(getState())) {
      return dispatch(fetchMaskingConfiguration())
    }
  }
}

export function saveMaskingConfiguration (maskingConfiguration) {
  return {
    type: SAVE_MASKING_CONFIGURATION,
    maskingConfiguration
  }
}

function requestMaskingConfiguration () {
  return {
    type: REQUEST_MASKING_CONFIGURATION
  }
}

const processConfiguration = (options) => {
  options = options.options
  const categories = {}

  for (const key in options) {
    const keyOptions = options[key]
    const category = keyOptions.category
    if (!Object.hasOwn(categories, category)) {
      categories[category] = []
    }

    categories[category].push({
      id: key,
      description: keyOptions.description,
      value: keyOptions.value,
      type: getType(keyOptions.value)
    })
  }

  return categories
}

const getType = (value) => {
  const valueType = typeof value

  if (valueType === 'number') return 'number'
  if (valueType === 'string') return 'text'
  if (valueType === 'boolean') return 'checkbox'

  return 'text'
}

function fetchMaskingConfiguration () {
  return dispatch => {
    dispatch(requestMaskingConfiguration())
    apiFetch('api/information/configuration', { what: 'Unable to retrieve the configuration' }).then(json => {
      dispatch(saveMaskingConfiguration(processConfiguration(json)))
    }).catch(error => {
      dispatch(saveMaskingConfiguration(undefined))
      dispatch(reportError(error))
    })
  }
}

function shouldFetchMaskingConfiguration (state) {
  const configurations = state.configurations

  return !configurations.isFetching
}

export const SAVE_DIFF_CONFIGURATION = 'SAVE_DIFF_CONFIGURATION'
export const REQUEST_DIFF_CONFIGURATION = 'REQUEST_DIFF_CONFIGURATION'

export function getDiffConfigurationIfNeeded () {
  return (dispatch, getState) => {
    if (shouldFetchDiffConfiguration(getState())) {
      return dispatch(fetchDiffConfiguration())
    }
  }
}

function shouldFetchDiffConfiguration (state) {
  return true
}

function fetchDiffConfiguration () {
  return dispatch => {
    dispatch(requestDiffConfiguration())
    dispatch(saveDiffConfiguration(0.01))
  }
}

export function saveDiffConfiguration (value) {
  return {
    type: SAVE_DIFF_CONFIGURATION,
    value
  }
}

function requestDiffConfiguration () {
  return {
    type: REQUEST_DIFF_CONFIGURATION
  }
}

export const REQUEST_ANONYMIZATION_PROVIDER_CONFIGURATION = 'REQUEST_ANONYMIZATION_PROVIDER_CONFIGURATION'
export const SAVE_ANONYMIZATION_PROVIDER_CONFIGURATION = 'SAVE_ANONYMIZATION_PROVIDER_CONFIGURATION'

export function saveAnonymizationProviderConfiguration (configuration, algorithmName) {
  return {
    type: SAVE_ANONYMIZATION_PROVIDER_CONFIGURATION,
    algorithmName,
    configuration
  }
}

export function fetchAnonymizationProviderConfigurationIfNeeded (algorithmName) {
  return (dispatch, getState) => {
    if (shouldFetchAnonymizationProviderConfiguration(getState(), algorithmName)) {
      dispatch(fetchAnonymizationProviderConfiguration(algorithmName))
    }
  }
}

function shouldFetchAnonymizationProviderConfiguration (state, algorithmName) {
  const { configurations } = state
  if (!configurations) return true
  else if (!configurations[algorithmName]) return true
  else if (!configurations[algorithmName].isFetching) return true
  return false
}

function requestAnonymizationProviderConfiguration (algorithmName) {
  return {
    type: REQUEST_ANONYMIZATION_PROVIDER_CONFIGURATION,
    algorithmName
  }
}

function fetchAnonymizationProviderConfiguration (algorithmName) {
  return dispatch => {
    dispatch(requestAnonymizationProviderConfiguration(algorithmName))
    apiFetch(`api/information/defaultConfiguration/${encodeURIComponent(algorithmName)}`, { what: `Unable to retrieve the configuration of ${algorithmName}` })
      .then(json => dispatch(saveAnonymizationProviderConfiguration(json, algorithmName)))
      .catch(error => {
        dispatch(saveAnonymizationProviderConfiguration(undefined, algorithmName))
        dispatch(reportError(error))
      })
  }
}
