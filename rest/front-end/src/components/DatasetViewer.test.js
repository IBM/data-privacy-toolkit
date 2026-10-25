/* global it expect */

import React from 'react'
import { render } from '@testing-library/react'
import DatasetViewer from './DatasetViewer'

it('testing component only, without internal structures', () => {
  const { container } = render(<DatasetViewer dataset={[]} />)
  expect(container).not.toBeUndefined()
})

it('renders empty dataset', () => {
  const { container } = render(<DatasetViewer dataset={[]} />)
  expect(container).not.toBeUndefined()
})

it('renders the dataset of a row using shallow', () => {
  const testDataset = [[0, 1, 2, 3]]
  const { container } = render(<DatasetViewer dataset={testDataset} />)
  expect(container).not.toBeUndefined()
})

it('renders the dataset of a row', () => {
  const testDataset = [[0, 1, 2, 3]]
  const { container } = render(<DatasetViewer dataset={testDataset} />)
  expect(container).not.toBeUndefined()
})
