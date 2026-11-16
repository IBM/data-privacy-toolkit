import React from 'react'

import { Container, Nav, NavDropdown, Navbar } from 'react-bootstrap'

import Icon from './Icon'

const title = (icon, text) => <span><Icon name={icon} /> {text}</span>

const NavBar = () => (
  <Navbar expand='lg' fixed='top' variant='dark' className='app-navbar' collapseOnSelect>
    <Container fluid>
      <Navbar.Brand href='https://www.research.ibm.com/labs/ireland/'>IBM Research Europe - Ireland</Navbar.Brand>
      <Navbar.Toggle aria-controls='main-navigation' />
      <Navbar.Collapse id='main-navigation'>
        <Nav>
          <Nav.Link href='#/'><Icon name='house-fill' /> Home</Nav.Link>
          <NavDropdown id='information_drop_down' title={title('info-circle-fill', 'Information')}>
            <NavDropdown.Item href='#/identifiers'>Identifiers</NavDropdown.Item>
            <NavDropdown.Item href='#/maskingProviders'>Masking Providers</NavDropdown.Item>
            <NavDropdown.Item href='#/anonymizationProviders'>Anonymization Providers</NavDropdown.Item>
          </NavDropdown>
          <NavDropdown id='configuration_drop_down' title={title('gear-fill', 'Configurations')}>
            <NavDropdown.Item href='#/configuration'>Masking Providers</NavDropdown.Item>
            <NavDropdown.Item href='#/configurationAnon'>Anonymization Algorithms</NavDropdown.Item>
          </NavDropdown>
          <NavDropdown id='demos_drop_down' title={title('laptop', 'Demos')}>
            <NavDropdown.Item href='#/workflow/loading'>Data Protection Workflow</NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item href='#/dicomMasking' disabled>DICOM Masking</NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item href='#/freetext' disabled>Free-text Pipeline</NavDropdown.Item>
          </NavDropdown>
        </Nav>
      </Navbar.Collapse>
    </Container>
  </Navbar>
)

export default NavBar
