import { describe, it, expect, vi, afterEach } from 'vitest'
import { legacy_createStore as createStore, combineReducers, applyMiddleware } from 'redux'
import { thunk } from 'redux-thunk'

import reducers from '../reducers'
import { identifyDataset } from './identify'
import { analyzeDataset } from './risk'
import { getIdentifiersIfNeeded } from './identifiers'
import { getMaskingConfigurationIfNeeded } from './configurations'
import { loadLocal } from './loading'
import { reportError } from './alerts'

const newStore = () => createStore(combineReducers(reducers), applyMiddleware(thunk))
const settle = () => new Promise(resolve => setTimeout(resolve, 20))

afterEach(() => {
  vi.restoreAllMocks()
})

describe('when the back-end fails', () => {
  it('identification: releases the page and tells the user', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(new Response('stack trace of the server', { status: 500 }))
    const store = newStore()

    store.dispatch(identifyDataset('a,b', false))
    expect(store.getState().workflow.isFetching).toBe(true)
    await settle()

    expect(store.getState().workflow.isFetching).toBe(false)
    expect(store.getState().alerts.map(a => a.message)).toEqual(['Unable to identify the column types (HTTP 500)'])
  })

  it('vulnerability detection: releases the page and tells the user (it used to fail silently)', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(new Response('', { status: 400 }))
    const store = newStore()

    store.dispatch(analyzeDataset('a,b', false, 5, 'DUCC'))
    await settle()

    expect(store.getState().workflow.isFetching).toBe(false)
    expect(store.getState().alerts).toHaveLength(1)
  })

  it('masking configuration: releases the page when the server cannot be reached (it used to hang)', async () => {
    vi.spyOn(window, 'fetch').mockRejectedValue(new TypeError('Failed to fetch'))
    const store = newStore()

    store.dispatch(getMaskingConfigurationIfNeeded())
    await settle()

    expect(store.getState().configurations.isFetching).toBe(false)
    expect(store.getState().alerts.map(a => a.message)).toEqual(['Unable to retrieve the configuration: the server cannot be reached'])
  })

  it('loading a bundled data set: releases the page (it used to hang)', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(new Response('', { status: 404 }))
    const store = newStore()

    store.dispatch(loadLocal('missing.csv'))
    await settle()

    expect(store.getState().workflow.isFetching).toBe(false)
    expect(store.getState().alerts.map(a => a.message)).toEqual(['Unable to load missing.csv (HTTP 404)'])
  })

  it('information lookup: reports a single message for repeated failures', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(new Response('', { status: 503 }))
    const store = newStore()

    store.dispatch(getIdentifiersIfNeeded())
    await settle()
    store.dispatch(getIdentifiersIfNeeded())
    await settle()

    expect(store.getState().alerts).toHaveLength(1)
  })
})

describe('reportError', () => {
  it('does not show the details of a defect to the user', () => {
    const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {})
    const store = newStore()

    store.dispatch(reportError(new TypeError("Cannot read properties of undefined (reading 'secret')")))

    expect(store.getState().alerts.map(a => a.message)).toEqual(['Unexpected error. Please try again.'])
    expect(consoleError).toHaveBeenCalled()
  })
})
