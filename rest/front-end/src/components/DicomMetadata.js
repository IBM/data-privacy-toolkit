import React from 'react'

const DicomMetadata = ({metadata = [], modified = []}) => {
  const attributes = metadata.map(({
      name,
      vrName,
      value
    }, i) => {
    const className = modified.indexOf(name) !== -1 ? 'bg-danger' : ''
    return (
      <div className={`row ${className}`} key={i}>
        <div className='col-md-4'>{name}</div>
        <div className='col-md-4'>{vrName}</div>
        <div className='col-md-4'>{value}</div>
      </div>
    )
  }
  )

  return (
    <div className='container-fluid'>
      <div className='row'>
        <div className='col-md-4'><b>Tag</b></div>
        <div className='col-md-4'><b>VR</b></div>
        <div className='col-md-4'><b>Value</b></div>
      </div>
      {attributes}
    </div>
  )
}

export default DicomMetadata
