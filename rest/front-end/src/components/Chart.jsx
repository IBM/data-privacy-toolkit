import React, { Component } from 'react'
import Highcharts from 'highcharts'

export default class Chart extends Component {
  componentDidMount () {
    this.chart = new Highcharts.Chart(this.props.container || 'chart', this.props.options)
  }

  componentWillUnmount () {
    if (this.chart) {
      this.chart.destroy()
    }
  }

  componentDidUpdate (previousProps) {
    if (this.chart && previousProps.options !== this.props.options) {
      this.chart.update(this.props.options)
    }
  }

  render () {
    return (<div id={this.props.container || 'chart'} className='riskUtilityChart' />)
  }
}
