import {compress, decompress} from 'lz-string'

const storage = window.localStorage || {
  getItem: () => null,
  setItem: () => undefined,
  removeItem: () => undefined
}

export const loadState = () => {
  try {
    const serializedKeys = storage.getItem('__keys__')
    if (serializedKeys !== null) {
      const state = {}
      const keys = JSON.parse(decompress(serializedKeys))

      for (const i in keys) {
        const key = keys[i]
        const serializedKData = storage.getItem(key)
        if (serializedKData !== null) {
          const kData = JSON.parse(decompress(serializedKData))

          state[key] = kData
        }
      }
      return state
    }
  } catch (err) {
  }
  return undefined
}

const whiteList = new Set([
  'workflow'
])

export const saveState = (state) => {
  try {
    const keys = Object.keys(state)

    const serializedKeys = compress(JSON.stringify(keys))
    storage.setItem('__keys__', serializedKeys)

    for (const i in keys) {
      const key = keys[i]
      if (!whiteList.has(key)) continue
      const kData = state[key]
      const serializedKData = compress(JSON.stringify(kData))
      storage.setItem(key, serializedKData)
    }
  } catch (err) {
    console.log('ERROR ' + err)
  }
}

export const clearState = () => {
  try {
    storage.removeItem('state')
  } catch (err) {
    console.log('ERROR ' + err)
  }
}
