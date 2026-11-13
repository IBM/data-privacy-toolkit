import React from 'react'
import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'

import { Alerts } from './Alerts'

describe('Alerts', () => {
  it('shows nothing when there is nothing to report', () => {
    render(<Alerts alerts={[]} onDismiss={() => {}} />)

    expect(screen.queryByRole('alert')).toBeNull()
  })

  it('shows every message, and dismisses the one that is closed', () => {
    const onDismiss = vi.fn()
    render(<Alerts alerts={[{ id: 1, message: 'one failed' }, { id: 2, message: 'two failed' }]} onDismiss={onDismiss} />)

    expect(screen.getAllByRole('alert')).toHaveLength(2)

    fireEvent.click(screen.getAllByLabelText('Close alert')[1])

    expect(onDismiss).toHaveBeenCalledWith(2)
  })

  it('renders the message as text, never as markup', () => {
    render(<Alerts alerts={[{ id: 1, message: '<img src=x onerror=alert(1)>' }]} onDismiss={() => {}} />)

    expect(screen.getByRole('alert').querySelector('img')).toBeNull()
    expect(screen.getByText('<img src=x onerror=alert(1)>')).toBeTruthy()
  })
})
