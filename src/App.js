import React from "react";
import "./App.css";
import {BrowserRouter as Router, Switch, Route, Link} from 'react-router-dom'
import data from "./data/dummy.json";
import MultiMap from "./components/MultiMap/MultiMap";
import Header from "./components/layouts/Header";
import Dashboard from './components/Dashboard/Dashboard'

function App() {
  return (
    <div className="App">
      <Router>
        <Header />
        <Switch>
          <Route exact path='/'>
            <MultiMap data={data}/>
          </Route>
          <Route path='/player'>
          </Route>
          <Route path='/dashboard'>
            <Dashboard data = {data}/>
          </Route>
        </Switch>
      </Router>
    </div>
  );
}

export default App;
