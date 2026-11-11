import { describe, it, expect } from 'vitest'

import { arrayToCSV, computeCDF, getFieldColor, DatasetToArray, CSVToArray, JSONToArray } from './utilities'
import { DIRECT_IDENTIFIER, K_QUASI_IDENTIFIER, E_QUASI_IDENTIFIER, SENSITIVE, NORMAL } from './actions/risk'

describe('arrayToCSV', () => {
  it('joins cells with commas and rows with newlines', () => {
    expect(arrayToCSV([[1, 2], [3, 4]])).toBe('1,2\n3,4')
  })
})

describe('getFieldColor', () => {
  it('maps every sensitivity to its css class', () => {
    expect(getFieldColor(DIRECT_IDENTIFIER)).toBe('directIdentifier')
    expect(getFieldColor(K_QUASI_IDENTIFIER)).toBe('kQuasiIdentifier')
    expect(getFieldColor(E_QUASI_IDENTIFIER)).toBe('eQuasiIdentifier')
    expect(getFieldColor(SENSITIVE)).toBe('sensitiveField')
    expect(getFieldColor(NORMAL)).toBe('')
    expect(getFieldColor('anything else')).toBe('')
  })
})

describe('computeCDF', () => {
  it('returns [ratio, risk] pairs sorted by ratio', () => {
    expect(computeCDF([0.5, 0.25, 0.25, 1])).toEqual([[50, 25], [75, 50], [100, 100]])
  })
})

describe('CSVToArray', () => {
  it('parses plain and quoted values', () => {
    expect(CSVToArray('a,b\n1,"x ""q"" y"', true)).toEqual([['a', 'b'], ['1', 'x "q" y']])
  })

  it('supports a custom delimiter', () => {
    expect(CSVToArray('a;b', false, ';')).toEqual([['a', 'b']])
  })
})

describe('JSONToArray', () => {
  it('flattens nested objects with dotted headers', () => {
    expect(DatasetToArray('a.json', '[{"a":1,"b":{"c":2}},{"a":3}]', true)).toEqual([['a', 'b.c'], [1, 2], [3, null]])
  })

  it('accepts a single object and no header', () => {
    expect(JSONToArray('{"x":{"y":1}}', false)).toEqual([[1]])
  })

  it('rejects null', () => {
    expect(() => JSONToArray('null')).toThrow()
  })
})

describe('DatasetToArray', () => {
  it('uses the CSV parser for .csv and .txt, case insensitive', () => {
    expect(DatasetToArray('a.CSV', '1,2')).toEqual([['1', '2']])
    expect(DatasetToArray('a.txt', '1,2')).toEqual([['1', '2']])
  })

  it('rejects unsupported extensions', () => {
    expect(() => DatasetToArray('a.xls', '')).toThrow('Dataset format not supported')
  })
})
