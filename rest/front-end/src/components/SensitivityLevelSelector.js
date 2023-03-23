import React from 'react'

import { NORMAL, DIRECT_IDENTIFIER, K_QUASI_IDENTIFIER, E_QUASI_IDENTIFIER, SENSITIVE } from '../actions/risk'

const sensitivityLevels = [
  {name: 'Normal', value: NORMAL},
  {name: 'Direct Identifier', value: DIRECT_IDENTIFIER},
  {name: 'k-Quasi Identifier', value: K_QUASI_IDENTIFIER},
  {name: 'e-Quasi Identifier', value: E_QUASI_IDENTIFIER},
  {name: 'Sensitive', value: SENSITIVE}
]

const SensitivityLevelSelector = ({selected, onChange}) => {
  const options = sensitivityLevels.map(({name, value}, i) => (<option key={i} value={value}>{name}</option>))

  return (
    <select className='fieldTypeSelect' value={selected} onChange={onChange}>
      {options}
    </select>
  )
}

export default SensitivityLevelSelector
