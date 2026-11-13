import React from 'react'

import { connect } from 'react-redux'
import Alert from 'react-bootstrap/Alert'

import { dismissAlert } from '../actions/alerts'

export const Alerts = ({ alerts, onDismiss }) => (
  <div className='alerts'>
    {alerts.map(({ id, message }) => (
      <Alert key={id} variant='danger' dismissible onClose={() => onDismiss(id)}>
        {message}
      </Alert>
    ))}
  </div>
)

const mapStateToProps = state => ({
  alerts: state.alerts
})

const mapDispatchToProps = dispatch => ({
  onDismiss: id => dispatch(dismissAlert(id))
})

export default connect(mapStateToProps, mapDispatchToProps)(Alerts)
