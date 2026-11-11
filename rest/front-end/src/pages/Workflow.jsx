import React, { Component } from 'react'

import { Route, Routes, Navigate } from 'react-router'

import withRouter from '../withRouter'

import { Nav, NavItem } from 'react-bootstrap'

import Loading from './Loading'
import Identify from './Identify'
import VulnerabilityDetection from './VulnerabilityDetection'
import ProtectDataset from './ProtectDataset'
import Anonymize from './Anonymize'

// the workflow is mounted at this path in App.jsx
const BASE_PATH = '/workflow'

const separator = {
  marginTop: '10px'
}

class Workflow extends Component {
  render () {
    const next = this.isStart() ? undefined : (<a className='btn btn-primary' onClick={this.back.bind(this)}><i className='fa fa-step-backward' /> Back</a>)
    return (
      <div className='container-fluid'>
        <h3>Privacy Protection Workflow</h3>
        <Nav bsStyle='pills' activeKey={this.getActiveKey()}>
          <NavItem eventKey={1}><i className='fa fa-upload' /> Load Data</NavItem>
          <NavItem eventKey={2}><i className='fa fa-search' /> Identify Types</NavItem>
          <NavItem eventKey={3}><i className='fa fa-search' /> Vulnerability Detection</NavItem>
          <NavItem eventKey={4}><i className='fa fa-lock' /> Data Protection</NavItem>
          <NavItem eventKey={5}><i className='fa fa-user-secret' /> Anonymization</NavItem>
        </Nav>
        <div style={separator} />
        <Routes>
          <Route path='loading' element={<Loading />} />
          <Route path='identify' element={<Identify />} />
          <Route path='risk' element={<VulnerabilityDetection />} />
          <Route path='protect' element={<ProtectDataset />} />
          <Route path='anonymize' element={<Anonymize />} />
          <Route path='*' element={<Navigate to={`${BASE_PATH}/loading`} replace />} />
        </Routes>
        <div className='btn-group pull-right' data-role='groups'>
          {next}
          <a
            className='btn btn-primary' onClick={
              (event) => {
                const nextState = this.getNextState()
                this.props.navigate(`${BASE_PATH}/${nextState}`)
              }
            }
          >{this.getNextLabel()} <i className='fa fa-step-forward' />
          </a>
        </div>
      </div>
    )
  }

  back (event) {
    this.props.navigate(-1)
  }

  getNextState () {
    const childPath = this.getChildPath()

    switch (childPath) {
      case 'loading': return 'identify'
      case 'identify': return 'risk'
      case 'risk': return 'protect'
      case 'protect': return 'anonymize'
      default: return 'loading'
    }
  }

  isStart () {
    return this.getChildPath() === 'loading'
  }

  getActiveKey () {
    const childPath = this.getChildPath()

    switch (childPath) {
      case 'loading': return 1
      case 'identify': return 2
      case 'risk': return 3
      case 'protect': return 4
      case 'anonymize': return 5
      default: return -1
    }
  }

  getNextLabel () {
    const childPath = this.getChildPath()

    switch (childPath) {
      case 'loading': return 'Start'
      default: return 'Next'
    }
  }

  getChildPath () {
    const matchedPath = new RegExp(`^${BASE_PATH}/(.*)`).exec(this.props.location.pathname)

    if (matchedPath === null || matchedPath.length <= 1) {
      return 'loading'
    } else {
      return matchedPath[1]
    }
  }
}

export default withRouter(Workflow)
