import React, { Component } from 'react'

import { Route, Redirect, Switch, withRouter } from 'react-router'

import { Nav, NavItem } from 'react-bootstrap'

import Loading from './Loading'
import Identify from './Identify'
import VulnerabilityDetection from './VulnerabilityDetection'
import ProtectDataset from './ProtectDataset'
import Anonymize from './Anonymize'

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
        <Switch>
          <Route path={this.props.match.path + '/loading'} component={Loading} />
          <Route path={this.props.match.path + '/identify'} component={Identify} />
          <Route path={this.props.match.path + '/risk'} component={VulnerabilityDetection} />
          <Route path={this.props.match.path + '/protect'} component={ProtectDataset} />
          <Route path={this.props.match.path + '/anonymize'} component={Anonymize} />
          <Redirect from={this.props.match.path} to={this.props.match.path + '/loading'} />
        </Switch>
        <div className='btn-group pull-right' data-role='groups'>
          {next}
          <a className='btn btn-primary' onClick={
            (event) => {
              const nextState = this.getNextState()
              this.props.history.push(`${this.props.match.path}/${nextState}`)
            }}>{this.getNextLabel()} <i className='fa fa-step-forward' /></a>
        </div>
      </div>
    )
  }

  back (event) {
    this.props.history.goBack()
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
    const path = this.props.match.path
    const pathRegEx = new RegExp(path + '/(.*)')
    const fullPath = this.props.location.pathname

    const matchedPath = pathRegEx.exec(fullPath)

    if (matchedPath === null || matchedPath.length <= 1) {
      return 'loading'
    } else {
      return matchedPath[1]
    }
  }
}

export default withRouter(Workflow)
