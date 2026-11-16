import React from 'react'

import SensitivityLevelSelector from './SensitivityLevelSelector'
import Icon from './Icon'

const SensitivitySelector = ({ providerName, fieldSensitivity, fieldContainerClasses, onChange, children }) => (
  <div className={fieldContainerClasses}>
    <div className='fieldTypeText'>{providerName} <Icon name='pencil-fill' /></div>
    <SensitivityLevelSelector selected={fieldSensitivity} onChange={onChange} />
  </div>
)

export default SensitivitySelector
