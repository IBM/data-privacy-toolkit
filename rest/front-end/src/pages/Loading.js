import React, { Component } from 'react'

import { connect } from 'react-redux'

import DatasetViewer from '../components/DatasetViewer'
import DatasetLoader from '../components/DatasetLoader'

import { loadLocal, loadRemote } from '../actions/loading'

class Loading extends Component {
  constructor (props) {
    super(props)

    this.state = {
      knownDatasets: [
        {name: 'healthcare-dataset.txt', id: 1},
        {name: 'simple.csv', id: 2},
        {name: 'washington.csv', id: 3},
        {name: '100.csv', id: 4},
        {name: 'compounds.csv', id: 5},
        {name: 'demo.csv', id: 6},
        {name: 'random1_height_weight.txt', id: 7}

        ,{name: 'Upload your own!', id: -1}
      ]
    }
  }

  render () {
    const dataset = this.props.dataset ? <DatasetViewer dataset={this.props.dataset} hasHeader={this.props.hasHeader} /> : undefined

    return (
      <div className='container-fluid'>
        <DatasetLoader knownDatasets={this.state.knownDatasets} onLoadLocal={(name) => this.props.loadLocal(name)} onLoadRemote={(file, hasHeader) => this.props.loadRemote(file, hasHeader)} />
        { dataset }
      </div>
    )
  }
}

const mapStateToProps = (state) => {
  return {
    dataset: state.workflow.isFetching ? undefined : state.workflow.dataset,
    hasHeader: state.workflow.isFetching ? undefined : state.workflow.hasHeader
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    loadLocal: (name) => {
      dispatch(loadLocal(name))
    },
    loadRemote: (file, hasHeader) => {
      dispatch(loadRemote(file, hasHeader))
    }
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Loading)
