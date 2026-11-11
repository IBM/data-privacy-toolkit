import React, { Component } from 'react'

import { connect } from 'react-redux'

import { getAnonymizationProvidersIfNeeded } from '../actions/anonymizationProviders'

import Features from '../components/Features'

class AnonymizationProviders extends Component {
  componentDidMount () {
    this.props.getAnonymizationProviders()
  }

  render () {
    return (
      <Features features={this.props.anonymizationProviders} type='Anonymization Providers' />
    )
  }
}

const mapStateToProps = (state) => {
  return {
    anonymizationProviders: state.anonymizationProviders.data || []
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    getAnonymizationProviders: () => dispatch(getAnonymizationProvidersIfNeeded())
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AnonymizationProviders)
