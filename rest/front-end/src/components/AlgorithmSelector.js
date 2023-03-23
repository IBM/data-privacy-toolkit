import React from 'react'

const AlgorithmSelector = ({id = '', algorithms, selected, onChange}) => {
  const options = algorithms.map((algorithm, index) => <option key={index} value={algorithm.name}>{algorithm.name}</option>)
  return (
    <select id={id} value={selected} onChange={onChange}>
      <option value=''>-- select an algorithm --</option>
      {options}
    </select>
  )
}

export default AlgorithmSelector
