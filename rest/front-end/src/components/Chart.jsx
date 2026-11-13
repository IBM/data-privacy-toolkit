/* global ResizeObserver */

import React, { useEffect, useRef } from 'react'

// Only the parts of ECharts that are used, to keep the bundle small. SVG rendering needs no <canvas>.
import * as echarts from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components'
import { SVGRenderer } from 'echarts/renderers'

echarts.use([LineChart, GridComponent, LegendComponent, TitleComponent, TooltipComponent, SVGRenderer])

/**
 * Draws `options` (ECharts options). `onPointClick(data)` is called with the data item of a clicked point.
 */
const Chart = ({ options, onPointClick, label = 'Chart' }) => {
  const container = useRef(null)
  const chart = useRef(null)
  const clickHandler = useRef(onPointClick)
  clickHandler.current = onPointClick

  useEffect(() => {
    const instance = echarts.init(container.current, undefined, { renderer: 'svg' })
    chart.current = instance
    instance.on('click', event => clickHandler.current?.(event.data))

    const observer = typeof ResizeObserver === 'undefined' ? undefined : new ResizeObserver(() => instance.resize())
    observer?.observe(container.current)

    return () => {
      observer?.disconnect()
      instance.dispose()
      chart.current = null
    }
  }, [])

  useEffect(() => {
    chart.current.setOption(options, true)
  }, [options])

  return <div ref={container} className='riskUtilityChart' role='img' aria-label={label} />
}

export default Chart
