import React, { Component } from 'react'
import { connect } from 'react-redux'

import DatasetViewer from '../components/DatasetViewer'
import AlgorithmSelector from '../components/AlgorithmSelector'
import Legend from '../components/Legend'

import { getAnonymizationProvidersIfNeeded } from '../actions/anonymizationProviders'
import { getInformationLossMetricsIfNeeded } from '../actions/informationLossMetrics'
import { getMaskingProvidersIfNeeded } from '../actions/maskingProviders'
import { NORMAL, DIRECT_IDENTIFIER, K_QUASI_IDENTIFIER, E_QUASI_IDENTIFIER, SENSITIVE } from '../actions/risk'
import { saveLinking } from '../actions/exploration'
import { anonymizeDataset } from '../actions/anonymize'

import { getFieldColor } from '../utilities'

const InformationLossSelector = ({id = '', algorithms, selected, onChange}) => {
  const options = algorithms.map((algorithm, index) => <option key={index} value={algorithm.shortName}>{algorithm.name}</option>)
  return (
    <select id={id} value={selected} onChange={onChange}>
      <option value=''>-- select an algorithm --</option>
      {options}
    </select>
  )
}
class Anonymize extends Component {
  constructor (props) {
    super(props)

    this.state = {
      selectedMetric: 'CP',
      selectedAlgorithm: 'OLA',
      k: props.k,
      suppression: props.suppression === undefined ? 10.0 : props.suppression
    }
  }

  render () {
    const headers = this.buildHeaderRow()
    const suppression = this.state.selectedAlgorithm === 'OLA' ? (
      <div className='form-group'>
        <label htmlFor='suppression'>Suppression:</label>
        <input id='suppression' type='number' value={this.state.suppression} min='0.0' max='20.0' step='0.01' onChange={event => {
          this.setState(
            Object.assign({}, this.state, {suppression: Number(event.target.value)})
          )
        }} />
      </div>
    ) : undefined

    return (
      <div className='container-fluid'>
        <Legend />
        <DatasetViewer dataset={this.props.anonymized || this.props.protected || []} hasHeader={this.props.hasHeader} headers={headers} />
        <div className='form-inline'>
          <div className='form-group'>
            <label htmlFor='algorithm'>Algorithm:</label>
            <AlgorithmSelector id='metric' algorithms={this.props.algorithms} selected={this.state.selectedAlgorithm} onChange={event => this.setState(Object.assign({}, this.state, {selectedAlgorithm: event.target.value}))} />
          </div>
          <div className='form-group'>
            <label htmlFor='kInputField'>k value:</label>
            <input id='kInputField' type='number' min='2' step='1' value={this.state.k} onChange={event => {
              this.setState(Object.assign({}, this.state, {k: Number(event.target.value)}))
            }} />
          </div>
          {suppression}
        </div>
        <div className='form-inline'>
          <div className='form-group'>
            <label htmlFor='metric'>Information Loss Metric:</label>
            <InformationLossSelector id='metric' algorithms={this.props.metrics} selected={this.state.selectedMetric} onChange={event => this.setState(Object.assign({}, this.state, {selectedMetric: event.target.value}))} />
          </div>
        </div>
        <div className='form-inline'>
          <div className='form-group'>
            <a className='btn btn-primary' onClick={event => {
              this.props.history.push('/exploration')
            }}><i className='fa fa-line-chart' /> Explore</a>
          </div>
          <div className='form-group'>
            <a className='btn btn-primary' onClick={event => {
              const options = {
                lossMetric: this.state.selectedMetric,
                k: this.state.k,
                suppressionRate: this.state.suppression,
                algorithmName: this.state.selectedAlgorithm,
                epsilon: this.state.epsilon
              }
              this.props.anonymizeData(
                this.props.protected, this.props.hasHeader, Array.from(this.props.kQuasi), Array.from(this.props.eQuasi), Array.from(this.props.sensitive), this.props.headers, options
              )
            }}><i className='fa fa-user-secret' /> Anonymize</a>
          </div>
        </div>
      </div>
    )
  }

  componentDidMount () {
    if (undefined === this.props.protected) {
      this.props.history.goBack()
    } else {
      if (this.props.metrics.length === 0) {
        this.props.getInformationLossMetrics()
      }
      if (this.props.algorithms.length === 0) {
        this.props.getAnonymizationProviders()
      }
      if (!this.props.providers) {
        this.props.getMaskingProviders()
      }

      this.props.updateLinking(
        Array.from(this.props.kQuasi).concat(Array.from(this.props.eQuasi))
      )
    }
  }

  getFieldSensitivity (fieldId) {
    const { direct, kQuasi, eQuasi, sensitive } = this.props

    if (direct.has(fieldId)) return DIRECT_IDENTIFIER
    if (kQuasi.has(fieldId)) return K_QUASI_IDENTIFIER
    if (eQuasi.has(fieldId)) return E_QUASI_IDENTIFIER
    if (sensitive.has(fieldId)) return SENSITIVE

    return NORMAL
  }

  buildHeaderRow () {
    if (this.props.protected && this.props.headers && this.props.providers) {
      return this.props.protected[0].map((v, i) => {
        const columnName = this.props.hasHeader ? v : `Column ${i}`;
        const provider = this.props.headers[columnName] || {}
        const providerName = provider.name || 'UNKNOWN'

        const fieldSensitivity = this.getFieldSensitivity(i)
        const fieldContainerClasses = `fieldTypeContainer ${getFieldColor(fieldSensitivity)}`

        const icon = `fa fa-${this.props.linkingData.has(i) ? 'link' : 'chain-broken'}`
        const linkIcon = (<i className={icon} onClick={event => {
          const newLinkingData = new Set(this.props.linkingData)

          if (this.props.linkingData.has(i)) {
            newLinkingData.delete(i)
          } else {
            newLinkingData.add(i)
          }

          this.props.updateLinking(newLinkingData)
        }} />)

        return (
          <div className={fieldContainerClasses}>
            <div className='fieldTypeText'>{linkIcon} {providerName}</div>
          </div>
        )
      })
    }
  }
}

const mapStateToProps = state => {
  return {
    hasHeader: Boolean(state.workflow.hasHeader),
    headers: state.workflow.identifiedTypes,
    direct: new Set(state.workflow.direct),
    eQuasi: new Set(state.workflow.eQuasi),
    kQuasi: new Set(state.workflow.kQuasi),
    sensitive: new Set(state.workflow.sensitive),
    k: state.workflow.k,
    suppression: state.workflow.suppression,
    providers: state.maskingProviders.data,
    protected: state.workflow.protectedDataset,
    anonymized: state.workflow.anonymizedDataset,
    diffConfiguration: state.configurations.epsilon,
    algorithms: Array.from(state.anonymizationProviders.data || []),
    metrics: Array.from(state.informationLossMetrics.data || []),
    hierarchies: Object.assign({}, state.hierarchies.data),
    linkingData: new Set(state.workflow.linkingData)
  }
}

const mapDispatchToProps = dispatch => {
  return {
    anonymizeData: (data, hasHeader, kQuasi, eQuasi, sensitive, types, options) => {
      dispatch(anonymizeDataset(data, hasHeader, kQuasi, eQuasi, sensitive, types, options))
    },
    getInformationLossMetrics: () => {
      dispatch(getInformationLossMetricsIfNeeded())
    },
    getAnonymizationProviders: () => {
      dispatch(getAnonymizationProvidersIfNeeded())
    },
    getMaskingProviders: () => {
      dispatch(getMaskingProvidersIfNeeded())
    },
    updateLinking: (linkingData) => {
      dispatch(saveLinking(linkingData))
    }
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Anonymize)
