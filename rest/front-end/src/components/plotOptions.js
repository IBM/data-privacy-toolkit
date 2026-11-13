// Turns the result of a risk/utility exploration into ECharts options.
//
// `data` is a list of results, one per (k, suppression) pair:
//   { k, suppression, realSuppression, bestNode, informationLoss: { [metric]: { value } }, riskMetricResults: { [name]: risk } }

const escapeHtml = value => String(value)
  .replace(/&/g, '&amp;')
  .replace(/</g, '&lt;')
  .replace(/>/g, '&gt;')
  .replace(/"/g, '&quot;')
  .replace(/'/g, '&#39;')

// ECharts renders tooltips as HTML, and bestNode comes from the back-end: it is escaped.
export const tooltipHtml = ({ seriesName, data }) => [
  `<b>${escapeHtml(seriesName)}</b>`,
  `k: ${escapeHtml(data.k)}`,
  `BestNode: ${escapeHtml(data.bestNode)}`,
  `Suppression: ${escapeHtml(data.realSuppression)}`
].join('<br />')

export function buildChartOptions (data, suppression, metric) {
  const rows = data.filter(result => result.suppression === suppression)
  const riskMetrics = Object.keys(data[0].riskMetricResults)

  const series = riskMetrics.map(name => ({
    type: 'line',
    name,
    data: rows
      .map(result => ({
        value: [result.informationLoss[metric].value, result.riskMetricResults[name]],
        k: result.k,
        suppression,
        bestNode: result.bestNode,
        realSuppression: result.realSuppression
      }))
      .sort((a, b) => a.value[0] - b.value[0])
  }))

  return {
    title: {
      text: 'Disclose Risk (Re-identification/linkage)',
      subtext: `Suppression: ${suppression}%`,
      left: 'center'
    },
    tooltip: { trigger: 'item', formatter: tooltipHtml },
    legend: { bottom: 0 },
    grid: { left: 60, right: 30, top: 70, bottom: 80 },
    xAxis: { type: 'value', name: `Utility Loss (${metric})`, nameLocation: 'middle', nameGap: 28, scale: true },
    yAxis: { type: 'value', name: 'Risk', scale: true },
    series
  }
}
