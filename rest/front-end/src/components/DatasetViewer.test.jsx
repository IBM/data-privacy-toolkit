import { describe, it, expect } from 'vitest'
import { render } from '@testing-library/react'

import DatasetViewer from './DatasetViewer'

describe('DatasetViewer', () => {
  it('renders an empty dataset', () => {
    const { container } = render(<DatasetViewer dataset={[]} />)

    expect(container.querySelectorAll('tbody tr')).toHaveLength(0)
  })

  it('renders one table row per dataset row, with a leading row number', () => {
    const { container } = render(<DatasetViewer dataset={[[0, 1, 2, 3], [4, 5, 6, 7]]} />)

    const rows = [...container.querySelectorAll('tbody tr')].map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))
    expect(rows).toEqual([['1', '0', '1', '2', '3'], ['2', '4', '5', '6', '7']])
  })
})
