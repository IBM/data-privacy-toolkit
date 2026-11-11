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
      await page.waitFor("[...document.querySelectorAll('#root .fa-pencil')].length > 0", { description: 'identified column types' })
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
  }
]

export { scenarios }
