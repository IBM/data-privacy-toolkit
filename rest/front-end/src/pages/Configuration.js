import React, { Component } from 'react'

import { connect } from 'react-redux'

import { saveMaskingConfiguration, getMaskingConfigurationIfNeeded } from '../actions/configurations'

const ConfigurationEntry = ({index, option, onChangeValue}) => (
  <div className='row'>
    <div className='col-xs-1'><span className='pull-right'>{index + 1}</span></div>
    <div className='col-xs-4'>{option.id}</div>
    <div className='col-xs-4'>{option.description}</div>
    <div className='col-xs-3'><input value={option.value || 'null'} type={option.type} onChange={(event) => {
      let value
      switch (option.type) {
        case 'number':
          value = Number(event.target.value)
          break
        case 'checkbox':
          value = Boolean(event.target.checked)
          break
        case 'text':
        default:
          value = event.target.value
          break
      }
      onChangeValue(index, value)
    }} /></div>
  </div>
)

const Category = ({category, options, onChangeValue}) => {
  const configurationEntries = options.map((option, index) => {
    return <ConfigurationEntry key={index} option={option} index={index} onChangeValue={(index, value) => {
      onChangeValue(category, index, value)
    }} />
  })

  return (
    <div className='panel panel-default'>
      <div className='panel-heading'><b>{category}</b></div>
      <div className='panel-content'>
        {configurationEntries}
      </div>
    </div>
  )
}

class Configuration extends Component {
  componentDidMount () {
    this.props.getConfiguration()
  }

  buildCategories () {
    const categories = this.props.categories
    const categoriesRow = Object.keys(categories).sort().map((category, index) => {
      return <Category key={index} category={category} options={categories[category]} onChangeValue={(category, index, value) => {
        const newEntry = Object.assign({}, categories[category][index], {value})
        const newCategory = categories[category].slice()
        const newCategories = Object.assign({}, categories)
        newCategory[index] = newEntry
        newCategories[category] = newCategory

        this.props.saveConfiguration(newCategories)
      }} />
    })
    return categoriesRow
  }

  render () {
    const rows = this.buildCategories()

    return (
      <div className='component-fluid'>
        <h3>System configuration (by category)</h3>
        {rows}
        <a className='btn btn-success' onClick={() => {
          this.props.getConfiguration()
        }}><i className='fa fa-refresh' /> Reset</a>
      </div>
    )
  }
}

const mapStateToProps = (state) => {
  return {
    categories: Object.assign({}, state.configurations.maskingConfiguration)
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    saveConfiguration: (configuration) => {
      dispatch(saveMaskingConfiguration(configuration))
    },
    getConfiguration: () => {
      dispatch(getMaskingConfigurationIfNeeded())
    }
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Configuration)
