import React from 'react'

const ShowColor = ({colorClass, text}) => (
  <div className={`colorShower ${colorClass}`}>
    <span><b>{text}</b></span>
  </div>
)

const Legend = () => (
  <div className='legend'>
    <ShowColor text={'Normal'} />
    <ShowColor colorClass={'directIdentifier'} text={'Direct Identifier'} />
    <ShowColor colorClass={'kQuasiIdentifier'} text={'k-Quasi Identifier'} />
    <ShowColor colorClass={'eQuasiIdentifier'} text={'e-Quasi Identifier'} />
    <ShowColor colorClass={'sensitiveField'} text={'Sensitive'} />
  </div>
)

export default Legend
