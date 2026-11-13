import React, { Component } from 'react'

import { Route, Routes, Navigate } from 'react-router'

import withRouter from '../withRouter'

import { Nav } from 'react-bootstrap'

import Loading from './Loading'
import Identify from './Identify'
import VulnerabilityDetection from './VulnerabilityDetection'
import ProtectDataset from './ProtectDataset'
import Anonymize from './Anonymize'
import Icon from '../components/Icon'

// the workflow is mounted at this path in App.jsx
const BASE_PATH = '/workflow'

const separator = {
  marginTop: '10px'
}

class Workflow extends Component {
  render () {
    const next = this.isStart() ? undefined : (<button type='button' className='btn btn-primary' onClick={this.back.bind(this)}><Icon name='skip-backward-fill' /> Back</button>)
    return (
      <div className='container-fluid'>
        <h3>Privacy Protection Workflow</h3>
        <Nav variant='pills' className='workflow-pills' activeKey={this.getActiveKey()} aria-label='Workflow steps'>
          <Nav.Item><Nav.Link as='span' eventKey={1}><Icon name='upload' /> Load Data</Nav.Link></Nav.Item>
          <Nav.Item><Nav.Link as='span' eventKey={2}><Icon name='search' /> Identify Types</Nav.Link></Nav.Item>
          <Nav.Item><Nav.Link as='span' eventKey={3}><Icon name='search' /> Vulnerability Detection</Nav.Link></Nav.Item>
          <Nav.Item><Nav.Link as='span' eventKey={4}><Icon name='lock-fill' /> Data Protection</Nav.Link></Nav.Item>
          <Nav.Item><Nav.Link as='span' eventKey={5}><Icon name='incognito' /> Anonymization</Nav.Link></Nav.Item>
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
        <div className='btn-group float-end' role='group'>
          {next}
          <button
            type='button'
            className='btn btn-primary' onClick={
              (event) => {
                const nextState = this.getNextState()
                this.props.navigate(`${BASE_PATH}/${nextState}`)
              }
            }
          >{this.getNextLabel()} <Icon name='skip-forward-fill' />
          </button>
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
