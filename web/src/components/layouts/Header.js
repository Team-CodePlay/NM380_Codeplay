import React from 'react';
import { Navbar, Nav, NavLink } from 'react-bootstrap';

const Header = () => {
  return (
    <div>

      <Navbar bg="primary" variant="dark" expand="lg" className="mx-auto">
        <Navbar.Brand expand="lg" href="/">
          <img src="/favicon.ico" width="30" alt="Product Logo" />
        </Navbar.Brand>
        <Navbar.Brand expand="lg" href="/" >GeoPlay</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="mr-auto">
            <NavLink href="/"><i className="fas fa-home"></i> Home</NavLink>
            <NavLink href="https://console.firebase.google.com/u/0/project/geoplay-codeplay/overview" target="_blank"><span style={{ marginBottom: "5px" }} className="iconify" data-icon="mdi:monitor-dashboard" data-inline="false"></span> Admin Panel</NavLink>
            {/* <NavLink href="/dashboard">Dashboard</NavLink> */}
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    </div>
  );
};

export default Header;