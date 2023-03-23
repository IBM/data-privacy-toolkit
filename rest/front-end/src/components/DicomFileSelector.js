import React from 'react'

const DicomFileSelector = ({onFileSelected}) => (
  <form>
    <div className='form-group'>
      <input className='form-control' type='file' id='dicom-file' name='dicomfile' size='40' onChange={event => {
        const selectedFiles = event.target.files

        if (selectedFiles.length) {
          onFileSelected(selectedFiles[0])
        }
      }} />
    </div>
  </form>
)

export default DicomFileSelector
