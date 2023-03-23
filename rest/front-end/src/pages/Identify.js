import React, { Component } from 'react'
import { connect } from 'react-redux'

import DatasetViewer from '../components/DatasetViewer'
import MaskingProvidersSelector from '../components/MaskingProvidersSelector'

import { identifyDataset, updateColumnValue } from '../actions/identify'
import { getMaskingProvidersIfNeeded } from '../actions/maskingProviders'

class Identify extends Component {
  constructor (props) {
    super(props)

    this.state = {
      sampleSize: 1000
    }
  }

  render () {
    return (
      <div className='container-fluid'>
        <DatasetViewer dataset={this.props.dataset || []} hasHeader={this.props.hasHeader} headers={this.buildHeaderRow()} />
        <div className='form-inline'>
          <div className='form-group'>
            <label htmlFor='sampleSize'>Sample size</label>
            <input value={this.state.sampleSize} className='sampleSizeInputField' onChange={event => {
              const sampleSize = Number(event.target.value)
              this.setState({sampleSize})
            }} type='number' step='1' max='10000' />
          </div>
          <div className='form-group'>
            <a className='btn btn-primary' onClick={event => {
              this.props.identifyDataset(this.props.dataset, this.props.hasHeader, this.state.sampleSize)
            }}><i className='fa fa-search' /> Identify</a>
          </div>
        </div>
      </div>
    )
  }

  componentDidMount () {
    if (undefined === this.props.dataset) {
      this.props.history.goBack()
    } else {
      this.props.getMaskingProviders()
      // if (false && undefined === this.props.headers) {
      // this.props.identifyDataset(this.props.dataset, this.props.hasHeader, this.state.sampleSize)
      // }
    }
  }

  buildHeaderRow () {
    if (!this.props.dataset) return undefined

    let headers = this.props.headers || {}
    return this.props.dataset[0].map((data, index) => {
      const columnName = this.props.hasHeader ? data : `Column ${index}`;
      const columnType = headers[columnName]
      const identifiedValue = columnType ? columnType.name : undefined
      return (<MaskingProvidersSelector selected={identifiedValue} providers={this.props.providers || []} key={index} onChange={(providerName) => {
        console.log(`${columnName} set to ${providerName}`)
        this.props.updateColumnValue(columnName, this.findMaskingProvider(providerName))
      }} />)
    })
  }

  findMaskingProvider (name) {
    return this.props.providers.find(provider => provider.name === name)
  }
}

const arrayToCSV = a => {
  return a.map(b => b.join()).join('\n')
}

const mapStateToProps = state => {
  return {
    dataset: state.workflow.dataset,
    hasHeader: state.workflow.hasHeader,
    headers: state.workflow.identifiedTypes,
    providers: state.maskingProviders.data,
    isFetching: state.workflow.isFetching
  }
}

const mapDispatchToProps = dispatch => {
  return {
    identifyDataset: (data, hasHeader, sampleSize) => {
      dispatch(identifyDataset(arrayToCSV(data), hasHeader, sampleSize))
    },
    getMaskingProviders: () => {
      dispatch(getMaskingProvidersIfNeeded())
    },
    updateColumnValue: (columnName, provider) => {
      dispatch(updateColumnValue(columnName, provider))
    }
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Identify)
