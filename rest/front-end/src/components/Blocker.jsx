import React, { Component } from 'react'

import { connect } from 'react-redux'

import watson from '../images/watson.gif'

class Blocker extends Component {
  render () {
    return (
      <div id='blocker' className={this.props.className}>
        <img className='imageInBlocker' src={watson} alt='Please wait...' />
      </div>
    )
  }
}

function isAnythingFetching (state = {}) {
  for (const reducer in state) {
    if (state[reducer].isFetching) return undefined
  }

  return 'invisible'
}

const mapStateToProps = state => {
  return {
    className: isAnythingFetching(state)
  }
}

export default connect(mapStateToProps)(Blocker)
