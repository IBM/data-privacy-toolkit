// Minimal Chrome DevTools Protocol client, built only on what Node >= 22 ships (fetch, WebSocket).
// It drives a headless Chrome that is already listening on CDP_HOST:CDP_PORT and records everything that
// indicates a broken page: uncaught exceptions, console errors/warnings, failed or 4xx/5xx requests and
// alert()/confirm() dialogs.
import { lookup } from 'node:dns/promises'
import { isIP } from 'node:net'

const CDP_PORT = Number(process.env.CDP_PORT ?? 9222)

// Chrome refuses DevTools requests whose Host header is not an IP address or localhost, so a host name
// such as host.docker.internal (used when the driver runs in a container) is resolved first.
async function resolveHost (host) {
  return isIP(host) ? host : (await lookup(host)).address
}

export class Page {
  #ws
  #nextId = 0
  #pending = new Map()
  #requests = new Map()
  #problems = []
  #loadWaiters = []
  #endpoint
  #targetId

  static async open () {
    const host = await resolveHost(process.env.CDP_HOST ?? '127.0.0.1')
    const endpoint = `${host}:${CDP_PORT}`
    const response = await fetch(`http://${endpoint}/json/new?about:blank`, { method: 'PUT' })
    if (!response.ok) throw new Error(`Cannot open a browser tab (${response.status})`)
    const target = await response.json()

    const page = new Page()
    page.#endpoint = endpoint
    page.#targetId = target.id
    const socketUrl = target.webSocketDebuggerUrl.replace(/^ws:\/\/[^/]+/, `ws://${endpoint}`)
    await page.#connect(socketUrl)
    for (const domain of ['Page', 'Runtime', 'Network', 'Log']) await page.send(`${domain}.enable`)
    return page
  }

  #connect (url) {
    this.#ws = new WebSocket(url)
    this.#ws.addEventListener('message', event => this.#onMessage(JSON.parse(event.data)))
    return new Promise((resolve, reject) => {
      this.#ws.addEventListener('open', resolve, { once: true })
      this.#ws.addEventListener('error', () => reject(new Error(`Cannot connect to ${url}`)), { once: true })
    })
  }

  send (method, params = {}) {
    const id = ++this.#nextId
    this.#ws.send(JSON.stringify({ id, method, params }))
    return new Promise((resolve, reject) => this.#pending.set(id, { resolve, reject, method }))
  }

  #onMessage (message) {
    if (message.id !== undefined) {
      const { resolve, reject, method } = this.#pending.get(message.id)
      this.#pending.delete(message.id)
      if (message.error) reject(new Error(`${method}: ${message.error.message}`))
      else resolve(message.result)
      return
    }

    const p = message.params
    switch (message.method) {
      case 'Page.loadEventFired':
        this.#loadWaiters.splice(0).forEach(resolve => resolve())
        break
      case 'Runtime.exceptionThrown':
        this.#problems.push(`uncaught exception: ${(p.exceptionDetails.exception?.description ?? p.exceptionDetails.text).split('\n')[0]}`)
        break
      case 'Runtime.consoleAPICalled':
        if (p.type === 'error' || p.type === 'warning') {
          const text = p.args.map(a => a.value ?? a.description ?? '').join(' ').split('\n')[0]
          this.#problems.push(`console.${p.type}: ${text.slice(0, 240)}`)
        }
        break
      case 'Network.requestWillBeSent':
        this.#requests.set(p.requestId, { method: p.request.method, url: p.request.url, status: undefined })
        break
      case 'Network.responseReceived':
        if (this.#requests.has(p.requestId)) this.#requests.get(p.requestId).status = p.response.status
        break
      case 'Network.loadingFailed': {
        const request = this.#requests.get(p.requestId)
        if (!p.canceled) this.#problems.push(`request failed: ${request?.method} ${request?.url} (${p.errorText})`)
        break
      }
      case 'Page.javascriptDialogOpening':
        this.#problems.push(`${p.type} dialog shown: ${p.message}`)
        this.send('Page.handleJavaScriptDialog', { accept: true }).catch(() => {})
        break
    }
  }

  /** Problems seen since the last call. Failing HTTP requests are included. */
  takeProblems () {
    const httpErrors = [...this.#requests.values()]
      .filter(r => r.status >= 400)
      .map(r => `HTTP ${r.status}: ${r.method} ${r.url}`)
    const problems = [...this.#problems, ...httpErrors]
    this.#problems = []
    this.#requests = new Map([...this.#requests].filter(([, r]) => r.status < 400 || r.status === undefined))
    return problems
  }

  /** Back-end calls (/api/...) seen so far, as `{ method, path, status }`. */
  apiCalls () {
    return [...this.#requests.values()]
      .filter(r => new URL(r.url).pathname.includes('/api/'))
      .map(r => ({ method: r.method, path: new URL(r.url).pathname + new URL(r.url).search, status: r.status }))
  }

  async goto (url) {
    const loaded = new Promise(resolve => this.#loadWaiters.push(resolve))
    await this.send('Page.navigate', { url })
    await loaded
  }

  async evaluate (expression) {
    const result = await this.send('Runtime.evaluate', { expression, returnByValue: true, awaitPromise: true })
    if (result.exceptionDetails) throw new Error(`evaluate failed: ${result.exceptionDetails.exception?.description ?? result.exceptionDetails.text}`)
    return result.result.value
  }

  /** Polls a JavaScript expression until it returns something truthy. */
  async waitFor (expression, { timeout = 10000, description = expression } = {}) {
    const deadline = Date.now() + timeout
    while (Date.now() < deadline) {
      if (await this.evaluate(expression)) return
      await new Promise(resolve => setTimeout(resolve, 100))
    }
    throw new Error(`Timed out after ${timeout} ms waiting for: ${description}`)
  }

  text () {
    return this.evaluate("document.getElementById('root')?.innerText ?? ''")
  }

  waitForText (text, options = {}) {
    return this.waitFor(`(document.getElementById('root')?.innerText ?? '').includes(${JSON.stringify(text)})`,
      { description: `text "${text}"`, ...options })
  }

  route (hash) {
    return this.evaluate(`location.hash = ${JSON.stringify(hash)}`)
  }

  /** Clicks the first button (not a navigation link) whose visible text contains `text`. */
  async click (text, selector = 'a.btn, button.btn') {
    const clicked = await this.evaluate(`(() => {
      const el = [...document.querySelectorAll(${JSON.stringify(selector)})]
        .find(e => e.innerText.trim().includes(${JSON.stringify(text)}))
      if (!el) return false
      el.click()
      return true
    })()`)
    if (!clicked) throw new Error(`Nothing to click matching "${text}" (${selector})`)
  }

  /**
   * Chooses an option of the n-th <select>, firing the change event React listens to. `label` is either text
   * contained in the option's label or the option's index.
   */
  async choose (label, selectIndex = 0) {
    const chosen = await this.evaluate(`(() => {
      const select = document.querySelectorAll('#root select')[${selectIndex}]
      const label = ${JSON.stringify(label)}
      const index = select && (typeof label === 'number' ? label : [...select.options].findIndex(o => o.text.includes(label)))
      if (!select || !select.options[index]) return false
      select.selectedIndex = index
      select.dispatchEvent(new Event('change', { bubbles: true }))
      return true
    })()`)
    if (!chosen) throw new Error(`No option "${label}" in select #${selectIndex}`)
  }

  async close () {
    this.#ws.close()
    await fetch(`http://${this.#endpoint}/json/close/${this.#targetId}`).catch(() => {})
  }
}
