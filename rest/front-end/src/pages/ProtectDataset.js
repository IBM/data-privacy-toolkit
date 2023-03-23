import React, { Component } from 'react'
import { connect } from 'react-redux'

import DatasetViewer from '../components/DatasetViewer'
import MaskingProvidersSelector from '../components/MaskingProvidersSelector'
import SelectableValue from '../components/SelectableValue'
import Legend from '../components/Legend'

import { protectDataset } from '../actions/protect'
import { updateColumnValue } from '../actions/identify'
import { getMaskingProvidersIfNeeded } from '../actions/maskingProviders'
import { getMaskingConfigurationIfNeeded } from '../actions/configurations'

class ProtectDataset extends Component {
  constructor (props) {
    super(props)

    this.state = {
      useCompound: false,
      useGlobalDiffPriv: false
    }
  }

  render () {
    const headers = this.buildHeaderRow()
    return (
      <div className='container-fluid'>
        <Legend />
        <DatasetViewer dataset={this.props.protected || this.props.dataset || []} hasHeader={this.props.hasHeader} headers={headers} />
        <div className='form-inline'>
          <div className='form-group'>
            <label htmlFor='useCompound'><input id='useCompound' type='checkbox' value={this.state.useCompound} onChange={event => {
              this.setState(
                Object.assign({}, this.state, {useCompound: event.target.checked})
              )
            }} /> Use Compound Data Masking</label>
          </div>
        </div>
        <div className='form-inline'>
          <div className='form-group'>
            <a className='btn btn-info' onClick={event => { this.props.history.push('/configuration') }}><i className='fa fa-gear' /> Configure</a>
          </div>
          <div className='form-group'>
            <a className='btn btn-primary' onClick={event => {
              this.props.protectDataset(this.props.dataset, this.props.hasHeader, this.state.useCompound, this.props.direct, this.props.headers, this.props.configuration, this.state.useGlobalDiffPriv)
            }}><i className='fa fa-lock' /> Protect</a>
          </div>
        </div>
      </div>
    )
  }

  componentDidMount () {
    if (undefined === this.props.dataset) {
      this.props.history.goBack()
      return
    }

    this.props.getMaskingProviders()
    if (undefined === this.props.configuration) {
      this.props.getMaskingConfiguration()
    }
  }

  buildHeaderRow () {
    if (!this.props.dataset) return undefined

    let headers = this.props.headers || {}
    return this.props.dataset[0].map((data, index) => {
      const columnName = this.props.hasHeader ? data : `Column ${index}`;
      const columnType = headers[columnName]
      const identifiedValue = columnType ? columnType.name : undefined
      const isDirect = this.props.direct.has(index) ? 'directIdentifier' : undefined

      if (isDirect) {
        return (
          <div key={index} className={isDirect}>
            <MaskingProvidersSelector selected={identifiedValue} providers={this.props.providers || []} onChange={(providerName) => {
              this.props.updateColumnValue(columnName, this.findMaskingProvider(providerName))
            }} />
          </div>
        )
      } else {
        const shownValue = (<span>{identifiedValue}</span>)
        return (
          <div key={index} className={isDirect}>
            <SelectableValue shownValue={shownValue} />
          </div>
        )
      }
    })
  }

  findMaskingProvider (name) {
    return this.props.providers.find(provider => provider.name === name)
  }
}

const mapStateToProps = state => {
  return {
    dataset: state.workflow.dataset,
    hasHeader: Boolean(state.workflow.hasHeader),
    headers: state.workflow.identifiedTypes,
    providers: state.maskingProviders.data,
    direct: new Set(state.workflow.direct),
    eQuasi: new Set(state.workflow.eQuasi),
    protected: state.workflow.protectedDataset,
    configuration: state.configurations.maskingConfiguration,
    diffConfiguration: state.configurations.epsilon
  }
}

const mapDispatchToProps = dispatch => {
  return {
    protectDataset: (data, hasHeader, useCompound, direct, types, configuration, useGlobalDiffPriv) => {
      dispatch(protectDataset(data, hasHeader, useCompound, direct, types, configuration, useGlobalDiffPriv))
    },
    getMaskingProviders: () => {
      dispatch(getMaskingProvidersIfNeeded())
    },
    updateColumnValue: (columnName, provider) => {
      dispatch(updateColumnValue(columnName, provider))
    },
    getMaskingConfiguration: () => {
      dispatch(getMaskingConfigurationIfNeeded())
    }
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ProtectDataset)
