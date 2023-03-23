import React from 'react'

const DicomImageViewer = ({imageData}) => {
  if (imageData) {
    return (<img src={imageData} alt='DICOMImage' />)
  } else {
    return (<span />)
  }
}

export default DicomImageViewer
