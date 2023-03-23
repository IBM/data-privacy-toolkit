import React, { Component } from 'react'

import _ from 'underscore'

import Chart from './Chart'

const Confirmation = ({k, suppression, onYes, onNo}) => (
  <div className='confirmationMessage'>
    <div className='confirmationHeader'>Use the following values for the anonymization step:</div>
    <div>
      <b>k:</b> <code>{k}</code>, <b>suppression:</b> <code>{suppression}</code>
    </div>
    <div style={{textAlign: 'right'}}>
      <a className='btn btn-success' onClick={onYes}>Yes</a> <a className='btn btn-danger' onClick={onNo}>No</a>
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

  extractSeries () {
    const suppression = this.state.selectedSuppression
    const series = []
    const serieLabels = Object.keys(this.props.data[0].riskMetricResults)

    const filterPredicate = obj => obj.suppression === suppression

    for (let i in serieLabels) {
      const label = serieLabels[i]

      if (label === 'HGEORM') continue

      const labelData = _.sortBy(
        this.props.data.filter(filterPredicate).map(testData => {
          const iloss = testData.informationLoss[this.state.selectedIloss]
          const k = testData.k
          const x = iloss.first
          return {
            name: `<b>k: ${k}<br />BestNode: ${testData.bestNode}<br />Suppression: ${testData.realSuppression}</b>`,
            x,
            k,
            suppression,
            y: testData.riskMetricResults[label]
          }
        }),
        obj => { return obj.x }
      )

      const serie = {
        type: 'line',
        allowPointSelect: true,
        data: labelData,
        name: label,
        events: {
          click: e => {
            const {k, suppression} = e.point
            this.setState(Object.assign({}, this.state, {showPopup: true, k, suppression}))
          }
        }
      }
      series.push(serie)
    }

    return series
  }

  render () {
    const suppressions = this.state.suppressions.map((suppression, i) => (<option key={i} value={suppression}>{suppression}</option>))
    const series = this.extractSeries()
    const metrics = this.state.metrics.map((metric, i) => (<option key={i} value={metric}>{metric}</option>))

    const options = {
      title: {
        text: '<b>Disclose Risk (Re-identification/linkage)</b>',
        x: -20
      },
      subtitle: {
        text: `Suppression: ${this.state.selectedSuppression}%`,
        x: -20 // center
      },
      xAxis: {
        allowDecimals: true,
        title: {
          text: `<b>Utility Loss (${this.state.selectedIloss})</b>`
        }
      },
      yAxis: {
        allowDecimals: true,
        title: {
          text: 'Risk'
        },
        endOnTick: true
      },
      series
    }
    const confirmation = this.state.showPopup ? (<Confirmation k={this.state.k} suppression={this.state.suppression} onYes={() => {
      this.props.selectedConfiguration(this.state.k, this.state.suppression)
      this.setState(Object.assign({}, this.state, {showPopup: false}))
    }} onNo={() => {
      this.setState(Object.assign({}, this.state, {showPopup: false}))
    }} />) : undefined

    return (
      <div>
        <Chart options={options} />
        <div className='form-inline'>
          <div className='form-group'>
            <label htmlFor='suppression'>Suppressions:</label>
            <select id='suppression' value={this.state.selectedSuppression} onChange={event => {
              const selectedSuppression = Number(event.target.value)
              this.setState(Object.assign({}, this.state, {selectedSuppression}))
            }}>
              {suppressions}
            </select>
          </div>
          <div className='form-group'>
            <label htmlFor='metric'>Information Loss Metric:</label>
            <select id='metric' value={this.state.selectedIloss} onChange={event => {
              const selectedIloss = event.target.value
              this.setState(Object.assign({}, this.state, {selectedIloss}))
            }}>
              {metrics}
            </select>
          </div>
        </div>
        {confirmation}
      </div>
    )
  }
}
