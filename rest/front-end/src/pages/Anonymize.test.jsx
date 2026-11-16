import React from 'react'
import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, fireEvent, act } from '@testing-library/react'
import { Provider } from 'react-redux'
import { MemoryRouter } from 'react-router'
import { legacy_createStore as createStore, combineReducers, applyMiddleware } from 'redux'
import { thunk } from 'redux-thunk'

import reducers from '../reducers'
import Anonymize from './Anonymize'
import { exploreIfNeeded } from '../actions/exploration'

afterEach(() => {
  vi.restoreAllMocks()
})

const storeWith = workflow => createStore(
  combineReducers(reducers),
  {
    workflow: {
      protectedDataset: [['a', 'b', 'c']],
      identifiedTypes: {},
      direct: [],
      eQuasi: [],
      kQuasi: [0],
      sensitive: [],
      hasHeader: false,
      ...workflow
    },
    // already loaded: the page does not need to call the back-end
    maskingProviders: { data: [{ name: 'NAME' }] },
    anonymizationProviders: { data: [{ name: 'OLA' }] },
    informationLossMetrics: { data: [{ name: 'Categorical Precision', shortName: 'CP' }] }
  },
  applyMiddleware(thunk)
)

const sentLinkingAttributes = async store => {
  const fetch = vi.spyOn(window, 'fetch').mockResolvedValue(new Response('[]'))
  const { workflow } = store.getState()

  await act(async () => {
    store.dispatch(exploreIfNeeded(
      false,
      { min: 2, max: 2, step: 1 },
      { min: 0, max: 0, step: 1 },
      {},
      [0],
      [],
      workflow.linkingData,
      'a,b,c'
    ))
  })

  return fetch.mock.calls[0][1].body.get('linkingAttributes')
}

describe('linking columns', () => {
  it('are kept as a list in the store after the user toggles one, so that they can be sent to the back-end', async () => {
    const store = storeWith({ linkingData: [0] })
    render(
      <Provider store={store}>
        <MemoryRouter><Anonymize /></MemoryRouter>
      </Provider>
    )

    // one toggle per column: [0] is linked, [1] and [2] are not
    const toggles = screen.getAllByTitle('Use this column to link records')
    expect(toggles).toHaveLength(3)
    fireEvent.click(toggles[1])

    expect(Array.isArray(store.getState().workflow.linkingData)).toBe(true)
    expect(store.getState().workflow.linkingData.slice().sort()).toEqual([0, 1])
    expect(await sentLinkingAttributes(store)).toBe('[0,1]')
  })

  it('can be unlinked again', async () => {
    const store = storeWith({ linkingData: [0] })
    render(
      <Provider store={store}>
        <MemoryRouter><Anonymize /></MemoryRouter>
      </Provider>
    )

    fireEvent.click(screen.getAllByTitle('Use this column to link records')[1])
    fireEvent.click(screen.getAllByTitle('Use this column to link records')[1])

    expect(store.getState().workflow.linkingData).toEqual([0])
  })
})

describe('exploration request', () => {
  it('sends the linking columns as a list even if it is given a Set', async () => {
    // JSON.stringify(new Set([1, 2])) is "{}", which the back-end rejects
    const store = storeWith({ linkingData: new Set([1, 2]) })

    expect(await sentLinkingAttributes(store)).toBe('[1,2]')
  })
})
