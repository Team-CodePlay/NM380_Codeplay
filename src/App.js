import React, { useState, useEffect } from "react";
import "./App.css";
import { BrowserRouter as Router, Switch, Route, Link } from 'react-router-dom';
import data from "./data/dummy.json";
import MultiMap from "./components/MultiMap/MultiMap";
import Header from "./components/layouts/Header";
import Spinner from "./components/utils/Spinner";
import firebase from './components/utils/firebase';
import Dashboard from './components/Dashboard/Dashboard';

function App() {
  const [data, setData] = useState();

  useEffect(() => {
    const fetchData = async () => {
      const videosRef = firebase
        .database()
        .ref("videos");
      const videosData = (await videosRef.once("value")).val();
      console.log("Firebase data:");
      console.log(videosData);
      setData(videosData);
    };
    fetchData();
  }, []);

  return (
    <div className="App">;
      <Router>
        <Header />
        <Switch>
          <Route exact path='/'>
            {data ? <MultiMap data={data} /> : <Spinner />}
          </Route>
          <Route path='/player'>
          </Route>
          <Route path='/dashboard'>
            <Dashboard data={data} />
          </Route>
        </Switch>
      </Router>
    </div >
  );
}

export default App;
