import React, { Component } from 'react'

import Chart from './Chart'
import { buildChartOptions } from './plotOptions'

const Confirmation = ({ k, suppression, onYes, onNo }) => (
  <div className='confirmationMessage'>
    <div className='confirmationHeader'>Use the following values for the anonymization step:</div>
    <div>
      <b>k:</b> <code>{k}</code>, <b>suppression:</b> <code>{suppression}</code>
    </div>
    <div style={{ textAlign: 'right' }}>
      <button type='button' className='btn btn-success' onClick={onYes}>Yes</button> <button type='button' className='btn btn-danger' onClick={onNo}>No</button>
    </div>
  </div>
)

export default class Plot extends Component {
  constructor (props) {
    super(props)

    const suppressions = Array.from(new Set(this.props.data.map(
      result => result.suppression
    )))
    const metrics = Object.keys(props.data[0].informationLoss).sort((a, b) => a.localeCompare(b))

    this.state = {
      metrics,
      suppressions,
      selectedSuppression: props.minSuppression,
      selectedIloss: 'NUE'
    }
  }

  render () {
    const suppressions = this.state.suppressions.map((suppression, i) => (<option key={i} value={suppression}>{suppression}</option>))
    const metrics = this.state.metrics.map((metric, i) => (<option key={i} value={metric}>{metric}</option>))

    const options = buildChartOptions(this.props.data, this.state.selectedSuppression, this.state.selectedIloss)
    const confirmation = this.state.showPopup
      ? (
        <Confirmation
          k={this.state.k} suppression={this.state.suppression} onYes={() => {
            this.props.selectedConfiguration(this.state.k, this.state.suppression)
            this.setState(Object.assign({}, this.state, { showPopup: false }))
          }} onNo={() => {
            this.setState(Object.assign({}, this.state, { showPopup: false }))
          }}
        />
        )
      : undefined

    return (
      <div>
        <Chart
          options={options}
          label='Risk against utility loss'
          onPointClick={({ k, suppression }) => this.setState(Object.assign({}, this.state, { showPopup: true, k, suppression }))}
        />
        <div className='form-inline'>
          <div className='form-group'>
            <label htmlFor='suppression'>Suppressions:</label>
            <select
              id='suppression' value={this.state.selectedSuppression} onChange={event => {
                const selectedSuppression = Number(event.target.value)
                this.setState(Object.assign({}, this.state, { selectedSuppression }))
              }}
            >
              {suppressions}
            </select>
          </div>
          <div className='form-group'>
            <label htmlFor='metric'>Information Loss Metric:</label>
            <select
              id='metric' value={this.state.selectedIloss} onChange={event => {
                const selectedIloss = event.target.value
                this.setState(Object.assign({}, this.state, { selectedIloss }))
              }}
            >
              {metrics}
            </select>
          </div>
        </div>
        {confirmation}
      </div>
    )
  }
}
