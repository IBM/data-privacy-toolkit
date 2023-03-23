import React, { Component } from 'react'

const separator = {
  marginTop: '10px'
}

class DatasetUploader extends Component {
  constructor (props) {
    super(props)

    this.state = {
      selectedFile: undefined,
      hasHeader: undefined
    }
  }

  onFileSelection (event) {
    const selectedFiles = event.target.files

    if (selectedFiles.length) {
      this.setState({ selectedFile: selectedFiles[0] })
    } else {
      this.setState({ selectedFile: undefined })
    }
  }

  onHasHeadersChange (event) {
    const hasHeader = event.target.checked

    this.setState({ hasHeader: hasHeader })
  }

  uploadFile (event) {
    if (this.state.selectedFile) {
      this.props.onLoad(this.state.selectedFile, this.state.hasHeader)
    }
  }

  render () {
    return (
      <div>
        <input type='file' className='form-control' onChange={this.onFileSelection.bind(this)} />
        <div className='checkbox'>
          <label>Has header: <input type='checkbox' onChange={this.onHasHeadersChange.bind(this)} /></label>
          <a className='btn btn-primary' onClick={this.uploadFile.bind(this)}><i className='fa fa-upload' /> Load the Selected Dataset</a>
        </div>
      </div>
    )
  }
}

export default class DatasetLoader extends Component {
  constructor (props) {
    super(props)

    this.state = {
      loadExternal: false
    }
  }

  buildOptions () {
    return this.props.knownDatasets.map((dataset, index) => (<option key={index} value={dataset.id}>{dataset.name}</option>))
  }

  onDatasetSelect (event) {
    const selectedIndex = event.target.selectedIndex

    if (selectedIndex === 0) return

    const selectedDataset = this.props.knownDatasets[selectedIndex - 1]

    if (selectedDataset.id !== -1) {
      this.props.onLoadLocal(selectedDataset.name)
    } else {
      this.setState({loadExternal: true})
    }
  }

  onLoad (file, hasHeader) {
    this.props.onLoadRemote(file, hasHeader)
  }

  render () {
    const options = this.buildOptions()

    const bringYourOwnDataset = this.state.loadExternal ? <DatasetUploader onLoad={this.onLoad.bind(this)} /> : undefined

    return (
      <div className='form-inline'>
        <div className='form-group'>
          <label htmlFor='datasetType'>Select a dataset:</label>
          <select onChange={this.onDatasetSelect.bind(this)}>
            <option value=''>-- select a dataset --</option>
            { options }
          </select>
          { bringYourOwnDataset }
          <div style={separator} />
        </div>
      </div>
    )
  }
}