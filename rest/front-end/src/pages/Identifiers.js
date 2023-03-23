import React, { Component } from 'react'

import { connect } from 'react-redux'

import { getIdentifiersIfNeeded } from '../actions/identifiers'

import Features from '../components/Features'

class Identifiers extends Component {
  componentDidMount () {
    this.props.getIdentifiers()
  }

  render () {
    return (
      <Features features={this.props.identifiers} type='Identifiers' />
    )
  }
}

const mapStateToProps = state => {
  return {
    identifiers: state.identifiers.data || []
  }
}

const mapDispatchToProps = dispatch => {
  return {
    getIdentifiers: () => dispatch(getIdentifiersIfNeeded())
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Identifiers)
