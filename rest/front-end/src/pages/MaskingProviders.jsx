import React, { Component } from 'react'

import { connect } from 'react-redux'

import { getMaskingProvidersIfNeeded } from '../actions/maskingProviders'

import Features from '../components/Features'

class MaskingProviders extends Component {
  componentDidMount () {
    this.props.getMaskingProviders()
  }

  render () {
    return (
      <Features features={this.props.maskingProviders} type='Masking Providers' />
    )
  }
}

const mapStateToProps = (state) => {
  return {
    maskingProviders: state.maskingProviders.data || []
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    getMaskingProviders: () => dispatch(getMaskingProvidersIfNeeded())
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MaskingProviders)
