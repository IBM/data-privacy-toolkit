import React from 'react'

import SelectableValue from './SelectableValue'

const MaskingProvidersSelector = ({selected = 'UNKNOWN', providers, onChange}) => (
  <SelectableValue shownValue={
    (<span>{
      selected
    } <i className='fa fa-pencil' /></span>)
  } hiddenValue={
    (<select value={selected} onChange={event => {
      const selectedIndex = event.target.selectedIndex
      const selectedValue = event.target.options[selectedIndex].value

      onChange(selectedValue)
    }}>
      <option key={0} value='UNKNOWN'>Unknown</option>
      {
     providers.sort((a, b) => {
       const A = a.name
       const B = b.name
       return A.localeCompare(B)
     }).map((provider, index) => <option key={index + 1} value={provider.name}>{provider.name}</option>)
      }
    </select>)
  } />)

export default MaskingProvidersSelector
