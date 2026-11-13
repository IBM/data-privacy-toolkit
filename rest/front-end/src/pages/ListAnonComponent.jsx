import React from 'react'

import { connect } from 'react-redux'

import { getAnonymizationProvidersIfNeeded } from '../actions/anonymizationProviders'
import { beautify } from '../utilities'
import Icon from '../components/Icon'

class ConfigurationEntry extends React.Component {
  render () {
    const x = this.props.configurationEntry

    return (
      <tr>
        <td>{this.props.index + 1}</td>
        <td>{x.friendlyName || beautify(x.name)}</td>
        <td>{x.description}</td>
        <td><a href={'#/configurationAnon/' + x.name} className='btn btn-primary'><Icon name='gear-fill' /></a></td>
      </tr>
    )
  }
}

class ListAnonComponent extends React.Component {
  componentDidMount () {
    this.props.getAlgorithms()
  }

  render () {
    const rows = this.props.algorithms.map((algorithm, index) => (<ConfigurationEntry key={index} index={index} configurationEntry={algorithm} />))
    return (
      <div className='component-fluid'>
        <h3>Available Anonymization Algorithms Configurations</h3>
        <table className='table table-border table-hover' summary=''>
          <thead>
            <tr>
              <th />
              <th>Name</th>
              <th>Description</th>
              <th>Edit</th>
            </tr>
          </thead>
          <tbody>
            {rows}
          </tbody>
        </table>
      </div>
    )
  }
}

const mapStateToProps = (state) => {
  return {
    algorithms: state.anonymizationProviders.data || []
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    getAlgorithms: () => {
      dispatch(getAnonymizationProvidersIfNeeded())
    }
  }
}
export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ListAnonComponent)
