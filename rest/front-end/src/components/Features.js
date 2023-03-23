import React from 'react'

const FeatureEntry = ({index, name, friendlyName = '', description}) => (
  <tr>
    <td>{index}</td>
    <td>{name}</td>
    <td>{friendlyName}</td>
    <td>{description}</td>
  </tr>
    )

const Features = ({features, type}) => (
  <div>
    <h3>Available {type}</h3>
    <table className='table table-border table-hover' summary=''>
      <thead>
        <tr>
          <th />
          <th>Name</th>
          <th>Friendly Name</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        {
    features.map((identifier, index) => <FeatureEntry key={index} index={index} name={identifier.name} friendlyName={identifier.friendlyName} description={identifier.description} />)
        }
      </tbody>
    </table>
  </div>
)

export default Features
