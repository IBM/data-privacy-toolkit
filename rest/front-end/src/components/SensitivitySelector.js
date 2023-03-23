import React from 'react'

import SensitivityLevelSelector from './SensitivityLevelSelector'

const SensitivitySelector = ({providerName, fieldSensitivity, fieldContainerClasses, onChange, children}) => (
  <div className={fieldContainerClasses}>
    <div className='fieldTypeText'>{providerName} <i className='fa fa-pencil' /></div>
    <SensitivityLevelSelector selected={fieldSensitivity} onChange={onChange} />
  </div>
  )

export default SensitivitySelector
