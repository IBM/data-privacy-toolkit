import React from 'react'
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { Provider } from 'react-redux'
import { MemoryRouter } from 'react-router'
import { legacy_createStore as createStore } from 'redux'

import Explore from './Explore'

// ECharts needs a layout engine that the test environment does not have
vi.mock('../components/Chart', () => ({ default: () => <div data-testid='chart' /> }))

const renderWith = (explorationReport, quasi = [0]) => {
  const state = {
    workflow: {
      protectedDataset: [['a', 'b']],
      direct: [],
      eQuasi: [],
      kQuasi: quasi,
      sensitive: [],
      hasHeader: false,
      identifiedTypes: {},
      linkingData: [0],
      explorationReport
    }
  }
  const store = createStore(() => state)

  return render(
    <Provider store={store}>
      <MemoryRouter><Explore /></MemoryRouter>
    </Provider>
  )
}

const result = {
  k: 2,
  suppression: 0,
  realSuppression: 0,
  bestNode: '[0, 1]',
  informationLoss: { NUE: { first: 1 } },
  riskMetricResults: { KRM: 0.5 }
}

describe('Explore page', () => {
  it('asks to compute the risk and utility when there is nothing to show yet', () => {
    renderWith(undefined)

    expect(screen.getByText('Compute risk utility')).toBeTruthy()
    expect(screen.queryByTestId('chart')).toBeNull()
    expect(screen.queryByRole('status')).toBeNull()
  })

  it('does not explore, and says why, when no column is a quasi-identifier', () => {
    renderWith(undefined, [])

    expect(screen.getByText('Compute risk utility').closest('button').disabled).toBe(true)
    expect(screen.getByRole('status').textContent).toContain('No column is marked as a quasi-identifier')
  })

  it('draws the chart when there are results', () => {
    renderWith([result])

    expect(screen.getByTestId('chart')).toBeTruthy()
    expect(screen.queryByRole('status')).toBeNull()
  })

  it('explains that nothing satisfies the parameters instead of failing, when the back-end returns no result', () => {
    renderWith([])

    expect(screen.queryByTestId('chart')).toBeNull()
    expect(screen.getByRole('status').textContent).toContain('No generalization satisfies')
  })
})
