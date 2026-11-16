import { describe, it, expect } from 'vitest'

import { explorationProblem } from './exploration'

const k = (min = 2, max = 20, step = 5) => ({ min, max, step })
const suppression = (min = 0, max = 20, step = 5) => ({ min, max, step })

describe('explorationProblem', () => {
  it('accepts the default values', () => {
    expect(explorationProblem(k(), suppression())).toBeUndefined()
  })

  it('accepts a single combination', () => {
    expect(explorationProblem(k(2, 2, 1), suppression(0, 0, 1))).toBeUndefined()
  })

  it.each([
    ['k below 2', k(1), suppression(), 'The minimum k must be at least 2.'],
    ['a maximum k below the minimum', k(5, 3), suppression(), 'The maximum k must not be smaller than the minimum.'],
    ['a k step of 0', k(2, 20, 0), suppression(), 'The k step must be at least 1.'],
    ['a fractional k', k(2.5), suppression(), 'The values of k must be integers.'],
    ['a negative suppression', k(), suppression(-1), 'The suppression must be between 0 and 100.'],
    ['a suppression above 100', k(), suppression(0, 101), 'The suppression must be between 0 and 100.'],
    ['a maximum suppression below the minimum', k(), suppression(10, 5), 'The maximum suppression must not be smaller than the minimum.'],
    ['a suppression step of 0', k(), suppression(0, 20, 0), 'The suppression step must be greater than 0.'],
    ['a value that is not a number', k(NaN), suppression(), 'Fill in all the values.']
  ])('rejects %s', (_, kValues, suppressionValues, message) => {
    expect(explorationProblem(kValues, suppressionValues)).toBe(message)
  })

  it('rejects more combinations than the back-end explores', () => {
    // 99 values of k by 2 suppressions
    expect(explorationProblem(k(2, 100, 1), suppression(0, 1, 1))).toMatch(/^Too many combinations/)
  })

  it('accepts as many combinations as the back-end explores', () => {
    // 10 values of k by 10 suppressions
    expect(explorationProblem(k(2, 11, 1), suppression(0, 9, 1))).toBeUndefined()
  })
})
