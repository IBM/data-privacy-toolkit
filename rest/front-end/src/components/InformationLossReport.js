import React from 'react'

const InformationLossReport = ({anonymisedInformationLoss}) => (
  <table className='table table-hover table-compact'>
    <thead>
      <tr>
        <th />
        <th>Original</th>
        <th>Only Protected</th>
        <th>Anonymised</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td><b>Information Loss</b></td>
        <td className='utility-warning'><span className='fullText'>{anonymisedInformationLoss}</span></td>
      </tr>
    </tbody>
  </table>
)

export default InformationLossReport
