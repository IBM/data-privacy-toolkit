import React, { Component } from 'react'

import { connect } from 'react-redux'

import { saveAnonymizationProviderConfiguration, fetchAnonymizationProviderConfigurationIfNeeded } from '../actions/configurations'

function beautify (name) {
  return name.toUpperCase()
}

const ConfigurationEntry = ({index, name, value, onChangeValue}) => (
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
    const selectedAlgorithm = this.props.match.params.anonymizationAlgorithm
    this.props.getAnonymizationConfiguration(selectedAlgorithm)
  }

  onChangeValue (key) {
    return (event) => {
      const state = Object.assign({}, this.props, this.state)
      console.log(state)
      state[this.props.anonymizationAlgorithm][key] = event.target.value
      this.setState(
        state
      )
    }
  }

  update () {
    console.log('Doing something, not right now though: ' + this.props.match.params.anonymizationAlgorithm)
    this.props.saveAnonymizationConfiguration(this.props.params.anonymizationAlgorithm, this.state.configuration)
  }

  buildPropertyList (configurations, algorithmName) {
    const entries = []

    if (configurations && configurations[algorithmName]) {
      const configuration = configurations[algorithmName].configuration || {}
      const keys = Object.keys(configuration)
      for (let i in keys) {
        const key = keys[i]
        const value = configuration[key]

        entries.push(<ConfigurationEntry key={entries.length} index={entries.length + 1} name={key} value={value} onChangeValue={this.onChangeValue(key)} />)
      }
    }

    return entries
  }

  render () {
    const rows = this.buildPropertyList(this.state || this.props.algorithmConfigurations, this.props.match.params.anonymizationAlgorithm)

    return (
      <div >
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
        <a className='btn btn-primary' onClick={this.update.bind(this)}><i className='fa fa-save' /> Save</a>
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

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ConfigurationForAnonymization)
