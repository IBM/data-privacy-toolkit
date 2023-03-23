export const ANALYZE_DATASET = 'ANALYZE_DATASET'
export const SAVE_RISK_IDENTIFICATION = 'SAVE_RISK_IDENTIFICATION'

export const NORMAL = 'NORMAL'
export const DIRECT_IDENTIFIER = 'DIRECT_IDENTIFIER'
export const K_QUASI_IDENTIFIER = 'K_QUASI_IDENTIFIER'
export const E_QUASI_IDENTIFIER = 'E_QUASI_IDENTIFIER'
export const SENSITIVE = 'SENSITIVE'

export function analyzeDataset (dataset, hasHeader, k, algorithmName) {
  return (dispatch, getState) => {
    if (needsIdentification(getState())) {
      dispatch(startAnalyze())

      const headers = new window.Headers()
      headers.set('Content-Type', 'text/csv')

      window.fetch(`api/feature/analyze/${algorithmName}/${k}/${Boolean(hasHeader)}`, {
        method: 'POST',
        headers,
        body: dataset
      }).then(response => {
        if (response.ok) {
          return response.json()
        } else {
          return undefined
        }
      }).then(data => dispatch(saveAnalyzedData(convertResponse(data), k)))
    }
  }
}

function convertResponse (data) {
  let direct = []
  let kQuasi = []
  const eQuasi = []
  const sensitive = []

  if (undefined !== data) {
    const moreSane = data.map(({itemSet}) => itemSet.items)
    direct = moreSane.filter(a => a.length === 1).reduce((a, c) => a.concat(c), [])
    kQuasi = Array.from(new Set(moreSane.filter(a => a.length !== 1).reduce((a, c) => a.concat(c), [])))
  }

  return {
    direct,
    kQuasi,
    eQuasi,
    sensitive
  }
}

function startAnalyze () {
  return {
    type: ANALYZE_DATASET
  }
}

function needsIdentification (state) {
  return !state.workflow.isFetching
}

function saveAnalyzedData ({direct, kQuasi, eQuasi, sensitive}, k) {
  return {
    type: SAVE_RISK_IDENTIFICATION,
    direct,
    kQuasi,
    eQuasi,
    sensitive,
    k
  }
}

export function updateColumnValue (fieldId, sensitivity) {
  return (dispatch, getState) => {
    const state = getState()
    const risk = state.workflow || {}
    const direct = new Set(risk.direct || [])
    const kQuasi = new Set(risk.kQuasi || [])
    const eQuasi = new Set(risk.eQuasi || [])
    const sensitive = new Set(risk.sensitive || [])

    direct.delete(fieldId)
    kQuasi.delete(fieldId)
    eQuasi.delete(fieldId)
    sensitive.delete(fieldId)

    switch (sensitivity) {
      case DIRECT_IDENTIFIER:
        direct.add(fieldId)
        break
      case K_QUASI_IDENTIFIER:
        kQuasi.add(fieldId)
        break
      case E_QUASI_IDENTIFIER:
        eQuasi.add(fieldId)
        break
      case SENSITIVE:
        sensitive.add(fieldId)
        break
      case NORMAL:
      default:
        break
    }

    dispatch(saveAnalyzedData({
      direct: Array.from(direct),
      kQuasi: Array.from(kQuasi),
      eQuasi: Array.from(eQuasi),
      sensitive: Array.from(sensitive)
    }, risk.k))
  }
}
