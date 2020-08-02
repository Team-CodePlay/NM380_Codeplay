import React from 'react';
import { Navbar, Nav, NavLink } from 'react-bootstrap';

const Header = () => {
  return (
    <div>
      <Navbar bg="primary" variant="dark" expand="lg">
        <Navbar.Brand href="/">
          <img src="/favicon.ico" width="30" alt="Product Logo" />
        </Navbar.Brand>
        <Navbar.Brand href="/">GeoPlay</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="mr-auto">
            <NavLink href="/">Home</NavLink>
            <NavLink href="/player">VideoPlayer</NavLink>
            <NavLink href="/dashboard">Dashboard</NavLink>
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    </div>
  );
};

export default Header;