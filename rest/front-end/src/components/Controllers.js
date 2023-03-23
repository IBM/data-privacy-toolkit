import React from 'react'

const Controllers = ({
  k, onKChange, suppression, onSuppressionChange
}) => (
  <div className='container-fluid'>
    <h4>K-values</h4>
    <div className='form-inline'>
      <div className='form-group'>
        <label htmlFor='minK'>Min</label>
        <input id='minK' onChange={event => {
          const value = Number(event.target.value)
          const id = event.target.id
          const property = id.substring(0, id.length - 1)

          const update = {}
          update[property] = value

          onKChange(update)
        }} className='kInput' value={k.min} type='number' min='2' max='100' step='1' />
      </div>
      <div className='form-group'>
        <label htmlFor='maxK'>Max</label>
        <input id='maxK' className='kInput' onChange={event => {
          const value = Number(event.target.value)
          const id = event.target.id
          const property = id.substring(0, id.length - 1)

          const update = {}
          update[property] = value

          onKChange(update)
        }} value={k.max} type='number' min='2' max='100' step='1' />
      </div>
      <div className='form-group'>
        <label htmlFor='stepK'>Step</label>
        <input id='stepK' onChange={event => {
          const value = Number(event.target.value)
          const id = event.target.id
          const property = id.substring(0, id.length - 1)

          const update = {}
          update[property] = value

          onKChange(update)
        }} className='kInput' value={k.step} type='number' min='1' max='100' step='1' />
      </div>
    </div>
    <h4>Suppression</h4>
    <div className='form-inline'>
      <div className='form-group'>
        <label htmlFor='minS'>Min</label>
        <input className='kInput' id='minS' onChange={onSuppressionChange} value={suppression.min} type='number' min='0.0' max='100.0' step='0.5' />
      </div>
      <div className='form-group'>
        <label htmlFor='maxS'>Max</label>
        <input className='kInput' id='maxS' onChange={onSuppressionChange} value={suppression.max} type='number' min='0.0' max='100.0' step='0.1' />
        <label htmlFor='stepS'>Step</label>
        <input className='kInput' id='stepS' onChange={onSuppressionChange} value={suppression.step} type='number' min='0.1' max='100.0' step='0.1' />
      </div>
    </div>
  </div>
)

export default Controllers
