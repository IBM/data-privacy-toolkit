export const REQUEST_ANNOTATION = 'REQUEST_ANNOTATION'
export const SAVE_ANNOTATION = 'SAVE_ANNOTATION'

export const REQUEST_NOTE = 'REQUEST_NOTE'
export const SAVE_NOTE = 'SAVE_NOTE'

export const REQUEST_MASKING_CONFIGURATION = 'REQUEST_MASKING_CONFIGURATION'
export const SAVE_MASKING_CONFIGURATION = 'SAVE_MASKING_CONFIGURATION'

export function annotateText (text) {
  return (dispatch, getState) => {
    if (shouldAnnotate(getState())) {
      dispatch(requestAnnotation())
      console.log(`Annotating with ${text}`)

      window.fetch(`api/freetext`, {
        method: 'POST',
        body: text
      }).then(response => {
        if (response.ok) {
          return response.json()
        }
        throw new Error(`Unable to perform annotation ${response.statusText}`)
      }).then(data => {
        dispatch(saveAnnotation(data))
      }).catch(error => {
        window.alert(error.message)
        dispatch(saveAnnotation(undefined))
      })
    }
  }
}

function shouldAnnotate (state) {
  const { freetext } = state
  return !freetext.isFetching
}

function requestAnnotation () {
  return {
    type: REQUEST_ANNOTATION
  }
}

function saveAnnotation (data) {
  return {
    type: SAVE_ANNOTATION,
    data
  }
}

function shouldLoadNote (state) {
  const { freetext } = state
  return !freetext.isFetching
}

function shouldLoadMaskingConfiguration (state) {
  const { freetext } = state
  return !freetext.isFetching
}

function requestLoadMaskingConfiguration () {
  return {
    type: REQUEST_MASKING_CONFIGURATION
  }
}

function saveMaskingConfiguration (data) {
  return {
    type: SAVE_MASKING_CONFIGURATION,
    data
  }
}

function requestLoadNote () {
  return {
    type: REQUEST_NOTE
  }
}

function saveNote (data) {
  return {
    type: SAVE_NOTE,
    data
  }
}

export function loadNoteFromFile (file) {
  return (dispatch, getState) => {
    if (shouldLoadNote(getState())) {
      dispatch(requestLoadNote())

      const reader = new window.FileReader()
      reader.onload = event => {
        dispatch(saveNote(event.target.result))
      }
      reader.readAsText(file)
    }
  }
}

export function loadMaskingConfiguration (file) {
  return (dispatch, getState) => {
    if (shouldLoadMaskingConfiguration(getState())) {
      dispatch(requestLoadMaskingConfiguration())

      const reader = new window.FileReader()
      reader.onload = event => {
        dispatch(saveMaskingConfiguration(JSON.parse(event.target.result)))
      }
      reader.readAsText(file)
    }
  }
}

function requestAnnotate () {
  return {
    type: REQUEST_ANNOTATION
  }
}

export function removeMaskingConfiguration () {
  return (dispatch, getState) => {
    dispatch(saveMaskingConfiguration(undefined))
  }
}

export function annotate (text, configuration) {
  return (dispatch, getState) => {
    if (shouldAnnotate(getState())) {
      dispatch(requestAnnotate())

      window.fetch('/api/freetext/annotate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          text,
          configuration: JSON.stringify(configuration)
        })
      }).then(response => {
        if (response.ok) {
          return response.text()
        }
      }).then(text => dispatch(saveNote(text)))
    }
  }
}
