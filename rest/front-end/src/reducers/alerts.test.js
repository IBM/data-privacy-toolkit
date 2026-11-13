import { describe, it, expect } from 'vitest'

import reducer from './alerts'
import { showAlert, dismissAlert } from '../actions/alerts'

describe('alerts reducer', () => {
  it('starts empty', () => {
    expect(reducer(undefined, { type: '@@INIT' })).toEqual([])
  })

  it('keeps the alerts in the order they were raised', () => {
    const first = showAlert('first')
    const second = showAlert('second')

    const state = [first, second].reduce(reducer, [])

    expect(state.map(a => a.message)).toEqual(['first', 'second'])
  })

  it('shows the same message only once', () => {
    const state = [showAlert('same'), showAlert('same')].reduce(reducer, [])

    expect(state).toHaveLength(1)
  })

  it('removes a dismissed alert and only that one', () => {
    const first = showAlert('first')
    const second = showAlert('second')
    const state = [first, second].reduce(reducer, [])

    expect(reducer(state, dismissAlert(first.id)).map(a => a.message)).toEqual(['second'])
  })
})
