import React, { Component } from 'react'
import { connect } from 'react-redux'

import withRouter from '../withRouter'

import Plot from '../components/Plot'
import Controllers from '../components/Controllers'

import { saveConfiguration, exploreIfNeeded } from '../actions/exploration'
import { arrayToCSV } from '../utilities'
import Icon from '../components/Icon'

class Explore extends Component {
  constructor (props) {
    super(props)

    this.state = {
      k: {
        min: 2,
        max: 20,
        step: 5
      },
      suppression: {
        min: 0.0,
        max: 20.0,
        step: 5.0
      }
    }
  }

  render () {
    const noSolution = Array.isArray(this.props.result) && this.props.result.length === 0
    const plot = this.props.result && !noSolution
      ? (
        <Plot
          minSuppression={this.state.suppression.min} data={this.props.result} selectedConfiguration={(k, suppression) => {
            this.props.saveConfiguration(k, suppression)
            this.props.navigate(-1)
          }}
        />
        )
      : undefined

    return (
      <div className='container-fluid'>
        <h1>Risk/Utility Exploration</h1>
        {plot}
        {noSolution && (
          <div className='alert alert-warning' role='status'>
            No generalization satisfies these values. Try smaller values of k or a larger suppression.
          </div>
        )}
        <Controllers
          k={this.state.k}
          onKChange={update => {
            const k = Object.assign({}, this.state.k, update)
            this.setState(Object.assign({}, this.state, { k }))
          }}
          suppression={this.state.suppression}
          onSuppressionChange={event => {
            const value = Number(event.target.value)
            const id = event.target.id
            const property = id.substring(0, id.length - 1)

            const update = {}
            update[property] = value
            const suppression = Object.assign({}, this.state.suppression, update)
            this.setState(Object.assign({}, this.state, { suppression }))
          }}
        />
        <button
          type='button'
          className='btn btn-primary float-end' onClick={event => {
            const sensitive = this.props.sensitive
            const vulnerabilities = this.props.eQuasi.concat(this.props.kQuasi)

            this.props.explore(
              this.props.hasHeader,
              this.state.k,
              this.state.suppression,
              this.props.headers,
              vulnerabilities,
              sensitive,
              this.props.linkingData,
              arrayToCSV(this.props.protected)
            )
          }}
        ><Icon name='gear-fill' /> Compute risk utility
        </button>
      </div>
    )
  }

  componentDidMount () {
    if (undefined === this.props.protected || undefined === this.props.direct || undefined === this.props.eQuasi || undefined === this.props.kQuasi || undefined === this.props.sensitive) {
      this.props.navigate(-1)
    }
  }
}

const mapStateToProps = state => {
  return {
    headers: state.workflow.identifiedTypes,
    protected: state.workflow.protectedDataset,
    hasHeader: state.workflow.hasHeader,
    direct: state.workflow.direct,
    eQuasi: state.workflow.eQuasi,
    kQuasi: state.workflow.kQuasi,
    sensitive: state.workflow.sensitive,
    result: state.workflow.explorationReport,
    linkingData: state.workflow.linkingData
  }
}

const mapDispatchToProps = dispatch => {
  return {
    saveConfiguration: (k, suppression) => {
      dispatch(saveConfiguration(k, suppression))
    },
    explore: (
      hasHeader,
      k,
      suppression,
      headers,
      vulnerabilities,
      sensitive,
      linkingData,
      data
    ) => {
      dispatch(exploreIfNeeded(
        hasHeader,
        k,
        suppression,
        headers,
        vulnerabilities,
        sensitive,
        linkingData,
        data
      ))
    }
  }
}

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps)(Explore))
