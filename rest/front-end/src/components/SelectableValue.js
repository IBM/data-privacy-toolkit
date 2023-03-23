import React from 'react'

const SelectableValue = ({shownValue, hiddenValue}) => (
  <div className='fieldTypeContainer'>
    <div className='fieldTypeText'>{shownValue}</div>
    <div className='fieldTypeSelect'>{hiddenValue}</div>
  </div>
)

export default SelectableValue
