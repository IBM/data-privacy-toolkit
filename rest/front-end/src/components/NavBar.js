import React from 'react'

import { Navbar, Nav, NavItem, NavDropdown, MenuItem } from 'react-bootstrap'

const generateTitle = (iconClass, text) => {
  return (
    <span><i className={'fa ' + iconClass} /> {text}</span>
  )
}

const NavBar = () => (
  <Navbar className='navbar navbar-default navbar-fixed-top'>
    <Navbar.Header>
      <Navbar.Brand>
        <a href='https://www.research.ibm.com/labs/ireland/'>IBM Research Europe - Ireland</a>
      </Navbar.Brand>
    </Navbar.Header>
    <Navbar.Collapse>
      <Nav>
        <NavItem href='#/'><i className='fa fa-home' /> Home</NavItem>
        <NavDropdown id='information_drop_down' title={generateTitle('fa-info-circle', 'Information')} >
          <MenuItem href='#/identifiers'>Identifiers</MenuItem>
          <MenuItem href='#/maskingProviders'>Masking Providers</MenuItem>
          <MenuItem href='#/anonymizationProviders'>Anonymization Providers</MenuItem>
        </NavDropdown>
        <NavDropdown id='configuration_drop_down' title={generateTitle('fa-gear', 'Configurations')} >
          <MenuItem href='#/configuration'>Masking Providers</MenuItem>
          <MenuItem href='#/configurationAnon'>Anonymization Algorithms</MenuItem>
        </NavDropdown>
        <NavDropdown id='demos_drop_down' title={generateTitle('fa-laptop', 'Demos')} >
          <MenuItem href='#/workflow/loading'>Data Protection Workflow</MenuItem>
          <MenuItem divider />
          <MenuItem href='#/dicomMasking' disabled>DICOM Masking</MenuItem>
          <MenuItem divider />
          <MenuItem href='#/freetext' disabled>Free-text Pipeline</MenuItem>
        </NavDropdown>
      </Nav>
    </Navbar.Collapse>
  </Navbar>
)

export default NavBar
