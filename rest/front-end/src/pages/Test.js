import React, { Component } from 'react'

import Chart from '../components/Chart'

export default class Test extends Component {
  render () {
    return (
      <div>
        <Chart options={{
          xAxis: {
            allowDecimals: true,
            min: 97
          },
          yAxis: {
            allowDecimals: true,
            type: 'logarithmic'
          },
          series: [
            {
              type: 'line',
              name: 'Original',
              data: [[0, 100], [90, 100], [100, 100]]
            }
          ]
        }} />
        <a className='btn btn-warning' onClick={() => {
          console.log('Testing')
        }}>Click me</a>
      </div>
    )
  }
}
