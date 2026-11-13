import { describe, it, expect } from 'vitest'

import { buildChartOptions, tooltipHtml } from './plotOptions'

const result = (k, suppression, nue, risks, bestNode = '[0, 1]') => ({
  k,
  suppression,
  realSuppression: suppression / 2,
  bestNode,
  informationLoss: { NUE: { value: nue }, CP: { value: nue * 10 } },
  riskMetricResults: risks
})

const data = [
  result(10, 0, 0.5, { KRM: 0.2, BINOM: 0.1 }),
  result(2, 0, 0.1, { KRM: 0.9, BINOM: 0.8 }),
  result(5, 0, 0.3, { KRM: 0.5, BINOM: 0.4 }),
  result(2, 5, 0.05, { KRM: 0.7, BINOM: 0.6 })
]

describe('buildChartOptions', () => {
  it('creates one line series per risk metric', () => {
    const { series } = buildChartOptions(data, 0, 'NUE')

    expect(series.map(s => s.name)).toEqual(['KRM', 'BINOM'])
    expect(series.every(s => s.type === 'line')).toBe(true)
  })

  it('keeps only the results of the selected suppression and sorts the points by utility loss', () => {
    const [krm] = buildChartOptions(data, 0, 'NUE').series

    expect(krm.data.map(point => point.value)).toEqual([[0.1, 0.9], [0.3, 0.5], [0.5, 0.2]])
    expect(krm.data.map(point => point.k)).toEqual([2, 5, 10])
  })

  it('uses the selected information loss metric on the x axis', () => {
    const [krm] = buildChartOptions(data, 0, 'CP').series
    const options = buildChartOptions(data, 0, 'CP')

    expect(krm.data.map(point => point.value[0])).toEqual([1, 3, 5])
    expect(options.xAxis.name).toBe('Utility Loss (CP)')
  })

  it('carries what the click handler needs on every point', () => {
    const [point] = buildChartOptions(data, 5, 'NUE').series[0].data

    expect(point).toMatchObject({ k: 2, suppression: 5, bestNode: '[0, 1]', realSuppression: 2.5 })
  })

  it('shows the selected suppression in the subtitle', () => {
    expect(buildChartOptions(data, 5, 'NUE').title.subtext).toBe('Suppression: 5%')
  })
})

describe('tooltipHtml', () => {
  it('lists k, best node and suppression', () => {
    expect(tooltipHtml({ seriesName: 'KRM', data: { k: 2, bestNode: '[0, 1]', realSuppression: 2.5 } }))
      .toBe('<b>KRM</b><br />k: 2<br />BestNode: [0, 1]<br />Suppression: 2.5')
  })

  it('escapes values that come from the back-end, because ECharts renders tooltips as HTML', () => {
    const html = tooltipHtml({ seriesName: '<img src=x onerror=alert(1)>', data: { k: 2, bestNode: '"><script>', realSuppression: 0 } })

    expect(html).not.toContain('<img')
    expect(html).not.toContain('<script>')
    expect(html).toContain('&lt;img src=x onerror=alert(1)&gt;')
  })
})
