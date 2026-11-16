import { describe, it, expect } from 'vitest'

import reducer from './workflow'
import { LOAD_DATASET, SAVE_DATASET } from '../actions/loading'
import { SAVE_RISK_IDENTIFICATION } from '../actions/risk'
import { SAVE_ANONYMIZED_DATASET } from '../actions/anonymize'
import { CLEAR_STATE } from '../actions/common'

describe('workflow reducer', () => {
  it('starts idle', () => {
    expect(reducer(undefined, { type: '@@INIT' })).toEqual({ isFetching: false })
  })

  it('marks the state as fetching while loading and stores the dataset when done', () => {
    const loading = reducer(undefined, { type: LOAD_DATASET })
    expect(loading.isFetching).toBe(true)

    const loaded = reducer(loading, { type: SAVE_DATASET, data: [[1]], hasHeader: true })
    expect(loaded).toEqual({ isFetching: false, dataset: [[1]], hasHeader: true })
  })

  it('stores the risk identification', () => {
    const state = reducer(undefined, { type: SAVE_RISK_IDENTIFICATION, direct: [0], kQuasi: [1], eQuasi: [], sensitive: [2], k: 5 })
    expect(state).toMatchObject({ direct: [0], kQuasi: [1], eQuasi: [], sensitive: [2], k: 5 })
  })

  it('stores the anonymized dataset', () => {
    const data = { dataset: [[1]], columnInformation: ['c'], informationLossResult: { NUE: 1 }, suppression: 2 }
    const state = reducer(undefined, { type: SAVE_ANONYMIZED_DATASET, data })
    expect(state).toMatchObject({ isFetching: false, anonymizedDataset: [[1]], columnInformation: ['c'], suppression: 2 })
  })

  it('clears the state', () => {
    const state = reducer({ isFetching: true, dataset: [[1]] }, { type: CLEAR_STATE })
    expect(state).toEqual({ isFetching: false })
  })

  it('does not mutate the previous state', () => {
    const before = { isFetching: false }
    reducer(before, { type: LOAD_DATASET })
    expect(before).toEqual({ isFetching: false })
  })
})
