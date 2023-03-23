import { DIRECT_IDENTIFIER, K_QUASI_IDENTIFIER, E_QUASI_IDENTIFIER, SENSITIVE, NORMAL } from './actions/risk'

import _ from 'lodash'

export const arrayToCSV = a => {
  return a.map(b => b.join()).join('\n')
}

export const beautify = (name) => {
  return name
}

export function getFieldColor (sensitivity) {
  switch (sensitivity) {
    case DIRECT_IDENTIFIER:
      return 'directIdentifier'
    case K_QUASI_IDENTIFIER:
      return 'kQuasiIdentifier'
    case E_QUASI_IDENTIFIER:
      return 'eQuasiIdentifier'
    case SENSITIVE:
      return 'sensitiveField'
    case NORMAL:
    default:
      return ''
  }
}

export function computeCDF (data) {
  var cdf = _.sortBy(data).map((value, idx) => {
    var count = idx + 1

    return {value, count}
  })

  cdf = Object.entries(cdf.reduce((a, b) => {
    if (a[b.value]) {
      a[b.value] = Math.max(a[b.value], b.count)
    } else {
      a[b.value] = b.count
    }

    return a
  }, {})).map((v) => {
    var risk = 100 * Number.parseFloat(v[0])
    var ratio = 100 * v[1] / data.length

    return [ ratio, risk ]
  })

  return _.sortBy(cdf, function (entry) {
    return entry[0]
  })
}

export function DatasetToArray (strName, strData, hasHeader, strDelimiter) {
    if (strName.toLowerCase().endsWith('.csv'))
        return CSVToArray(strData, hasHeader, strDelimiter);
    else if (strName.toLowerCase().endsWith('.txt'))
        return CSVToArray(strData, hasHeader, strDelimiter);
    else if (strName.toLowerCase().endsWith('.json'))
        return JSONToArray(strData, hasHeader);
    else
        throw new Error('Dataset format not supported');
}

export function CSVToArray (strData, hasHeader, strDelimiter) {
  // Check to see if the delimiter is defined. If not,
  // then default to comma.
  strDelimiter = (strDelimiter || ',')

  // Create a regular expression to parse the CSV values.
  const objPattern = new RegExp(
    (
      // Delimiters.
      '(\\' + strDelimiter + '|\\r?\\n|\\r|^)' +

      // Quoted fields.
      '(?:"([^"]*(?:""[^"]*)*)"|' +

      // Standard fields.
      '([^"\\' + strDelimiter + '\\r\\n]*))'
    ),
    'gi'
  )

  // Create an array to hold our data. Give the array
  // a default empty first row.
  const arrData = [[]]

  // Create an array to hold our individual pattern
  // matching groups.
  let arrMatches = null

  // Keep looping over the regular expression matches
  // until we can no longer find a match.
  while ((arrMatches = objPattern.exec(strData))) {
    // Get the delimiter that was found.
    const strMatchedDelimiter = arrMatches[ 1 ]

    // Check to see if the given delimiter has a length
    // (is not the start of string) and if it matches
    // field delimiter. If id does not, then we know
    // that this delimiter is a row delimiter.
    if (
      strMatchedDelimiter.length &&
      strMatchedDelimiter !== strDelimiter
    ) {
      // Since we have reached a new row of data,
      // add an empty row to our data array.
      arrData.push([])
    }

    let strMatchedValue

    // Now that we have our delimiter out of the way,
    // let's check to see which kind of value we
    // captured (quoted or unquoted).
    if (arrMatches[ 2 ]) {
      // We found a quoted value. When we capture
      // this value, unescape any double quotes.
      strMatchedValue = arrMatches[ 2 ].replace(new RegExp('""', 'g'), '"')
    } else {
      // We found a non-quoted value.
      strMatchedValue = arrMatches[ 3 ]
    }

    // Now that we have our value string, let's add
    // it to the data array.
    arrData[ arrData.length - 1 ].push(strMatchedValue)
  }

  // Return the parsed data.
  return (arrData)
}

export function JSONToArray (strData, hasHeader) {

    const dataset = JSON.parse(strData)

    if (dataset === null)
        throw new Error('Cannot parse JSON data. Is it a malformed JSON?');

    const headers = []
    const data = []

    function isPrimitive(test) {
      return (test !== Object(test))
    };

    function dumpObject(obj, prefix) {
      for(var prop in obj) {
        const current_path = prefix + prop
        const current_val = obj[prop]

        if (current_val === null || isPrimitive(current_val)) {
          if (headers.indexOf(current_path) === -1) headers.push(current_path);
          data[data.length-1][current_path] = current_val
        } else {
          dumpObject(current_val, current_path + '.')
        }
      }
    }

    // Handle datasets made of array of objects
    if (Array.isArray(dataset)) {
      dataset.forEach(obj => {
        data.push({});
        dumpObject(obj, '')
      })
    // But also datasets made of just one object
    } else {
      data.push({});
      dumpObject(dataset, '')
    }

    // Create an array to hold our data. Give the array
    // a default empty first row.
    const arrData = []

    if (hasHeader) {
      arrData.push([]);
      // Push headers as the first row
      headers.forEach(header => {
        arrData[0].push(header)
      })
    }

    // Then let's push all the other rows
    data.forEach(row => {
      arrData.push([])
      headers.forEach(header => {
        if (header in row) {
          arrData[ arrData.length - 1 ].push(row[header])
        } else {
          arrData[ arrData.length - 1 ].push(null)
        }
      })
    })

    // Return the parsed data.
    return arrData;
}
