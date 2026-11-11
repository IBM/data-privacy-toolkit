// Runs the UI smoke scenarios against a running application and a running headless Chrome.
//
//   APP_URL   address of the application served by the back-end  (default http://127.0.0.1:8080/)
//   CDP_HOST  host of the Chrome DevTools endpoint                (default 127.0.0.1)
//   CDP_PORT  port of the Chrome DevTools endpoint                (default 9222)
//
// Use run.sh to have the front-end built and the back-end and Chrome started and stopped for you.
import { Page } from './cdp.mjs'
import { scenarios } from './scenarios.mjs'

const APP_URL = process.env.APP_URL ?? 'http://127.0.0.1:8080/'
const STEP_SETTLE_MS = 250

const page = await Page.open()
let failures = 0

for (const scenario of scenarios) {
  const started = Date.now()
  let error

  // the first scenario starts from a fresh load, the others continue in the same page (state is kept on purpose,
  // the workflow scenarios depend on each other)
  if (scenario === scenarios[0]) await page.goto(APP_URL)

  const expectApiCall = async fragment => {
    await page.waitFor(`performance.getEntriesByType('resource').some(e => e.name.includes(${JSON.stringify(fragment)}))`,
      { description: `a call to ${fragment}` })
  }

  try {
    await scenario.run(page, { expectApiCall })
    await new Promise(resolve => setTimeout(resolve, STEP_SETTLE_MS)) // let late errors surface
  } catch (e) {
    error = e.message
  }

  const problems = page.takeProblems()
  const ok = error === undefined && problems.length === 0
  if (!ok) failures++

  console.log(`${ok ? 'PASS' : 'FAIL'}  ${scenario.name}  (${Date.now() - started} ms)`)
  if (error) {
    console.log(`      step: ${error}`)
    const visible = (await page.text()).replace(/\s+/g, ' ').trim()
    console.log(`      page shows: ${visible.slice(0, 300)}${visible.length > 300 ? '...' : ''}`)
  }
  for (const problem of problems) console.log(`      page: ${problem}`)
}

console.log('\nback-end calls made by the UI:')
for (const { method, path, status } of page.apiCalls()) console.log(`  ${status ?? '---'}  ${method.padEnd(4)} ${path}`)

await page.close()
console.log(`\n${scenarios.length - failures}/${scenarios.length} scenarios passed`)
process.exit(failures === 0 ? 0 : 1)
