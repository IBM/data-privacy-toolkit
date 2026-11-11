import React, { Component } from 'react'

import { Pager } from 'react-bootstrap'

class DatasetCell extends Component {
  render () {
    return (
      <td>{this.props.value}</td>
    )
  }
}

class DatasetRow extends Component {
  render () {
    const dataCell = this.props.data.map((value, index) => <DatasetCell key={index} value={value} />)
    const idx = this.props.first ? this.props.first : (this.props.index + 1)

    return (
      <tr>
        <DatasetCell value={idx} />
        {dataCell}
      </tr>
    )
  }
}

class DatasetHeaderCell extends Component {
  render () {
    return (
      <th>
        {this.props.value || ''}
      </th>
    )
  }
}

class DatasetHeader extends Component {
  render () {
    const headerCells = this.props.data.map((value, index) => <DatasetHeaderCell key={index} value={value} />)

    return (
      <tr>
        <DatasetHeaderCell />
        {headerCells}
      </tr>
    )
  }
}

export default class DatasetViewer extends Component {
  constructor (props) {
    super(props)

    this.state = {
      currentPage: 0,
      pageSize: this.props.pageSize || 10
    }
  }

  computeVisualizedRows (data) {
    const begin = this.state.currentPage * this.state.pageSize
    const end = Math.min(begin + this.state.pageSize, this.props.dataset.length)
    return data.slice(begin, end).map((row, index) => <DatasetRow key={index} index={begin + index} data={row} />)
  }

  goToPage (pageNumber) {
    this.setState(Object.assign({}, this.state, {
      currentPage: pageNumber
    }))
  }

  getLastPage () {
    return Math.ceil(
      this.props.dataset.length / this.state.pageSize
    )
  }

  goToFirstPage (event) {
    this.goToPage(0)
  }

  goToNextPage (event) {
    this.goToPage(
      Math.min(this.state.currentPage + 1, this.getLastPage() - 1)
    )
  }

  goToPreviousPage (event) {
    this.goToPage(
      Math.max(0, this.state.currentPage - 1)
    )
  }

  goToLastPage (event) {
    this.goToPage(this.getLastPage() - 1)
  }

  computeHeader (data) {
    if (data) {
      return <DatasetHeader data={data} />
    }
  }

  render () {
    const visualizedRows = this.computeVisualizedRows(this.props.hasHeader ? this.props.dataset.slice(1) : this.props.dataset)
    const headRow = this.computeHeader(this.props.headers)
    const columnHeadersRow = this.props.hasHeader ? this.computeHeader(this.props.dataset[0]) : undefined

    return (
      <div>
        <div className='table-responsive'>
          <table className='table table-border table-hover' summary=''>
            <thead>
              {headRow}
              {columnHeadersRow}
            </thead>
            <tbody>
              {visualizedRows}
            </tbody>
          </table>
        </div>
        <Pager>
          <Pager.Item onClick={this.goToFirstPage.bind(this)} aria-label='First'><i className='fa fa-step-backward' /></Pager.Item>
          <Pager.Item onClick={this.goToPreviousPage.bind(this)} aria-label='Previous'><i className='fa fa-play fa-flip-horizontal' /></Pager.Item>
          {this.state.currentPage + 1} / {this.getLastPage()}
          <Pager.Item onClick={this.goToNextPage.bind(this)} aria-label='Next'><i className='fa fa-play' /></Pager.Item>
          <Pager.Item onClick={this.goToLastPage.bind(this)} aria-label='Last'><i className='fa fa-step-forward' /></Pager.Item>
        </Pager>
      </div>
    )
  }
}
