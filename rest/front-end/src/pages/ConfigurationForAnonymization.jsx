import React, { Component } from 'react'

import { connect } from 'react-redux'

import withRouter from '../withRouter'

import { saveAnonymizationProviderConfiguration, fetchAnonymizationProviderConfigurationIfNeeded } from '../actions/configurations'
import Icon from '../components/Icon'

function beautify (name) {
  return name.toUpperCase()
}

const ConfigurationEntry = ({ index, name, value, onChangeValue }) => (
  <tr>
    <td>{index}</td>
    <td>{beautify(name)}</td>
    <td><input onChange={onChangeValue} value={value} /></td>
  </tr>
)

class ConfigurationForAnonymization extends Component {
  constructor (props) {
    super(props)

    this.state = {}
    this.onChangeValue = this.onChangeValue.bind(this)
  }

  componentDidMount () {
    const selectedAlgorithm = this.props.params.anonymizationAlgorithm
    this.props.getAnonymizationConfiguration(selectedAlgorithm)
  }

  onChangeValue (key) {
    return (event) => {
      const state = Object.assign({}, this.props, this.state)
      state[this.props.anonymizationAlgorithm][key] = event.target.value
      this.setState(
        state
      )
    }
  }

  update () {
    this.props.saveAnonymizationConfiguration(this.props.params.anonymizationAlgorithm, this.state.configuration)
  }

  buildPropertyList (configurations, algorithmName) {
    const entries = []

    if (configurations && configurations[algorithmName]) {
      const configuration = configurations[algorithmName].configuration || {}
      const keys = Object.keys(configuration)
      for (const i in keys) {
        const key = keys[i]
        const value = configuration[key]

        entries.push(<ConfigurationEntry key={entries.length} index={entries.length + 1} name={key} value={value} onChangeValue={this.onChangeValue(key)} />)
      }
    }

    return entries
  }

  render () {
    const rows = this.buildPropertyList(this.state || this.props.algorithmConfigurations, this.props.params.anonymizationAlgorithm)

    return (
      <div>
        <table className='table table-border table-hover' summary=''>
          <thead>
            <tr>
              <th />
              <th>Parameter</th>
              <th>Value</th>
            </tr>
          </thead>
          <tbody>
            {rows}
          </tbody>
        </table>
        <button type='button' className='btn btn-primary' onClick={this.update.bind(this)}><Icon name='floppy-fill' /> Save</button>
      </div>
    )
  }
}

function mapStateToProps (state) {
  return {
    algorithmConfigurations: state.configurations.anonymizationProviders
  }
}

function mapDispatchToProps (dispatch) {
  return {
    saveAnonymizationConfiguration: (algorithmName, configuration) => {
      dispatch(saveAnonymizationProviderConfiguration(algorithmName, configuration))
    },
    getAnonymizationConfiguration: (algorithmName) => {
      dispatch(fetchAnonymizationProviderConfigurationIfNeeded(algorithmName))
    }
  }
}

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps
)(ConfigurationForAnonymization))
