// UI smoke scenarios. Each scenario drives the real application, served by the real Spring Boot back-end, in a
// real (headless) browser. A scenario fails when a step throws, or when the page reports a problem while it ran:
// uncaught exception, console error/warning, failed request, HTTP >= 400 or alert() dialog.
//
// Add a scenario by appending to `scenarios`. Helpers are documented in cdp.mjs.

const SIMPLE_DATASET = 'simple.csv'

const scenarios = [
  {
    name: 'home page renders',
    async run (page) {
      await page.waitForText('Demo introduction')
    }
  },

  ...[
    ['/identifiers', 'api/information/identifiers'],
    ['/maskingProviders', 'api/information/maskingProviders'],
    ['/anonymizationProviders', 'api/information/anonymizationProviders'],
    ['/configuration', 'api/information/configuration']
  ].map(([route, api]) => ({
    name: `information page ${route} loads from ${api}`,
    async run (page, { expectApiCall }) {
      await page.route(`#${route}`)
      await expectApiCall(api)
    }
  })),

  {
    name: 'unknown route shows the not-found page',
    async run (page) {
      await page.route('#/does-not-exist')
      await page.waitForText('Page not found')
    }
  },

  {
    name: 'exploration without protected data goes back instead of crashing',
    async run (page) {
      // runs before the workflow scenarios: it needs an empty application state
      await page.route('#/exploration')
      await page.waitFor("location.hash !== '#/exploration'", { description: 'the guard to navigate back' })
    }
  },

  {
    name: `workflow: load ${SIMPLE_DATASET}`,
    async run (page) {
      await page.route('#/workflow/loading')
      await page.waitForText('Select a dataset')
      await page.choose(SIMPLE_DATASET)
      await page.waitForText('john@gmail.com')
    }
  },

  {
    name: 'workflow: identify column types (POST api/feature/identify)',
    async run (page, { expectApiCall }) {
      await page.click('Start')
      await page.waitForText('Sample size')
      await page.click('Identify')
      await expectApiCall('api/feature/identify/false')
      // the identified types are shown in the header selectors of the dataset
      await page.waitFor("[...document.querySelectorAll('#root .fieldTypeText')].some(e => e.innerText.includes('PHONE'))", { description: 'the PHONE column type found by the back-end' })
    }
  },

  {
    name: 'workflow: vulnerability detection (POST api/feature/analyze)',
    async run (page, { expectApiCall }) {
      await page.click('Next')
      await page.waitForText('Find Privacy Vulnerabilities')
      await page.choose(1, 0) // first real algorithm, after the placeholder
      await page.click('Find Privacy Vulnerabilities')
      await expectApiCall('api/feature/analyze/')
    }
  },

  {
    name: 'workflow: data protection (POST api/feature/mask)',
    async run (page, { expectApiCall }) {
      await page.click('Next')
      await page.waitForText('Use Compound Data Masking')
      await page.click('Protect')
      await expectApiCall('api/feature/mask/')
    }
  },

  {
    name: 'workflow: anonymization page renders (GET anonymization providers)',
    async run (page) {
      await page.click('Next')
      await page.waitForText('Explore')
    }
  },

  {
    name: 'exploration: risk/utility chart is drawn and a point can be chosen (POST api/feature/explore)',
    async run (page, { expectApiCall }) {
      await page.route('#/exploration')
      await page.waitForText('Compute risk utility')
      await page.click('Compute risk utility')
      await expectApiCall('api/feature/explore/false')

      // The chart is an SVG drawn with an animation. Points are the small filled symbols above the legend (axis ticks
      // have no fill); the legend's own symbols are below them.
      const points = `(() => {
        const chart = document.querySelector('.riskUtilityChart')
        const legendTop = chart.getBoundingClientRect().bottom - 60
        return [...chart.querySelectorAll('svg path')]
          .map(p => ({ box: p.getBoundingClientRect(), fill: p.getAttribute('fill'), stroke: p.getAttribute('stroke') }))
          .filter(p => p.fill && p.fill !== 'none' && p.box.width > 0 && p.box.width < 14 && p.box.bottom < legendTop)
      })()`
      // The data set has 3 rows, so only the smallest k can be satisfied: one point per risk metric.
      await page.waitFor(`${points}.length >= 1`, { description: 'a point in the chart' })

      const [x, y] = await page.evaluate(`(() => { const r = ${points}[0].box; return [r.x + r.width / 2, r.y + r.height / 2] })()`)
      await page.clickAt(x, y)
      await page.waitForText('Use the following values for the anonymization step')
      await page.click('Yes', 'button.btn')
      await page.waitFor("location.hash !== '#/exploration'", { description: 'to leave the exploration page after choosing' })
    }
  },

  {
    name: 'workflow: Back returns to the previous step (router history)',
    async run (page) {
      await page.click('Back')
      await page.waitForText('Use Compound Data Masking')
    }
  },

  {
    name: 'workflow: Configure opens the masking configuration (router navigation)',
    async run (page, { expectApiCall }) {
      await page.click('Configure')
      await page.waitFor("location.hash === '#/configuration'", { description: 'the #/configuration route' })
      await page.waitForText('System configuration')
    }
  },

  {
    name: 'anonymization algorithm configuration uses the route parameter',
    async run (page, { expectApiCall }) {
      await page.route('#/configurationAnon')
      await page.waitFor("!!document.querySelector('#root a[href=\"#/configurationAnon/OLA\"]')", { description: 'the list of algorithms' })
      await page.click('', 'a.btn[href="#/configurationAnon/OLA"]')
      await expectApiCall('api/information/defaultConfiguration/OLA')
    }
  },

  {
    name: 'a failing back-end is reported in the page, which stays usable (no dialog, no stuck overlay)',
    async run (page) {
      const endpoint = 'api/information/configuration'
      page.expectFailure(endpoint)
      await page.stub(endpoint, { message: 'java.lang.IllegalStateException: details that must not be shown' }, { status: 500 })
      try {
        await page.route('#/configuration')
        await page.waitForText('Unable to retrieve the configuration (HTTP 500)')

        if ((await page.text()).includes('IllegalStateException')) throw new Error('the server message is shown to the user')
        await page.waitFor("document.getElementById('blocker').classList.contains('invisible')", { description: 'the please-wait overlay to go away' })

        await page.click('', '.alerts .btn-close')
        await page.waitFor("document.querySelectorAll('.alerts .alert').length === 0", { description: 'the dismissed alert to disappear' })
      } finally {
        await page.unstub(endpoint)
      }

      // the same page works again once the back-end does
      await page.click('Reset')
      await page.waitFor("document.querySelectorAll('#root .panel').length > 0", { description: 'the configuration to be shown' })
    }
  }
]

export { scenarios }
