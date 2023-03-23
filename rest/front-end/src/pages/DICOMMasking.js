import React, { Component } from 'react'
import { connect } from 'react-redux'

import { loadDicomFile, maskDicomFile } from '../actions/dicom'

import DicomImageViewer from '../components/DicomImageViewer'
import DicomFileSelector from '../components/DicomFileSelector'
import DicomMetadata from '../components/DicomMetadata'

class DICOMMasking extends Component {
  render () {
    const mask = this.props.original ? (
      <a className='btn btn-primary' onClick={
          _ => {
            if (this.props.original) {
              this.props.maskFile(this.props.original)
            }
          }
        }><i className='fa fa-lock' /> Mask</a>
    ) : undefined

    const modifiedAttributes = this.computeDifference()

    return (
      <div className='container-fluid'>
        <h3>DICOM Masking</h3>

        <DicomFileSelector onFileSelected={file => this.props.loadFile(file)} />
        { mask }

        <div className='row'>
          <div className='col-md-6'>
            <b>Original</b>
          </div>
          <div className='col-md-6'>
            <b>Masked</b>
          </div>
        </div>
        <div className='row'>
          <div className='col-md-6'>
            <DicomImageViewer imageData={this.props.originalImage} />
          </div>
          <div className='col-md-6'>
            <DicomImageViewer imageData={this.props.maskedImage} />
          </div>
        </div>
        <div className='row'>
          <div className='col-md-6'>
            <DicomMetadata metadata={this.props.originalMetadata} modified={modifiedAttributes} />
          </div>
          <div className='col-md-6'>
            <DicomMetadata metadata={this.props.maskedMetadata} modified={modifiedAttributes} />
          </div>
        </div>
      </div>
    )
  }

  computeDifference () {
    const { maskedMetadata, originalMetadata } = this.props
    if (undefined === maskedMetadata || undefined === originalMetadata) {
      return []
    }

    const diff = []

    for (let i = 0; i < maskedMetadata.length; i += 1) {
      const m = maskedMetadata[i]
      for (let j = 0; j < originalMetadata.length; j += 1) {
        const o = originalMetadata[j]

        if (m.name === o.name && m.value !== o.value) {
          diff.push(m.name)
        }
      }
    }

    return diff
  }
}

const mapStateToProps = state => {
  const { dicom } = state
  const { original, originalMetadata, originalImage, maskedImage, maskedMetadata } = dicom
  return {
    original,
    originalImage,
    originalMetadata,
    maskedImage,
    maskedMetadata
  }
}

const mapDispatchToProps = dispatch => {
  return {
    loadFile: file => {
      dispatch(loadDicomFile(file))
    },
    maskFile: file => {
      dispatch(maskDicomFile(file))
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(DICOMMasking)
