/* global it expect */

import React from 'react'
import ReactDOM from 'react-dom'

import { shallow } from 'enzyme'
import renderer from 'react-test-renderer'

import DatasetViewer from './DatasetViewer'

it('testing component only, withtout internal structures', () => {
  const component = shallow(<DatasetViewer dataset={[]} />)

  expect(component).not.toBeUndefined()
})

it('renders empty dataset', () => {
  const component = renderer.create(<DatasetViewer dataset={[]} />)

  expect(component).not.toBeUndefined()
})

it('renders the dataset of a row using shallow', () => {
  const testDataset = [[0, 1, 2, 3]]

  const component = shallow(<DatasetViewer dataset={testDataset} />)

  expect(component).not.toBeUndefined()
})

it('renders the dataset of a row', () => {
  const div = document.createElement('div')

  const testDataset = [[0, 1, 2, 3]]

  ReactDOM.render(<DatasetViewer dataset={testDataset} />, div)
})
