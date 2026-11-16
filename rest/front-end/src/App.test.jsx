import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'

import App from './App'

describe('App', () => {
  it('renders the home page without crashing', () => {
    render(<App />)

    expect(screen.getByText('Demo introduction')).toBeTruthy()
  })

  it('shows the not-found page for an unknown route', () => {
    window.location.hash = '#/does-not-exist'
    render(<App />)

    expect(screen.getByText(/Page not found/)).toBeTruthy()
  })

  it('redirects an unknown workflow step to the loading step', () => {
    window.location.hash = '#/workflow/unknown-step'
    render(<App />)

    expect(screen.getByText('Privacy Protection Workflow')).toBeTruthy()
    expect(window.location.hash).toBe('#/workflow/loading')
  })
})
