import React, { useState, useEffect } from "react";
import "./App.css";
import { BrowserRouter as Router, Switch, Route, Link } from 'react-router-dom';
import MultiMap from "./components/MultiMap/MultiMap";
import Header from "./components/layouts/Header";
import Spinner from "./components/utils/Spinner";
import firebase from './components/utils/firebase';
import Dashboard from './components/Dashboard/Dashboard';
import LoadVideo from "./components/VideoPlayer/LoadVideo";

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
    <div className="App">
      <Header />
      <Router>
        <Switch>
          <Route exact path='/'>
            {data ? <MultiMap data={data} /> : <Spinner />}
          </Route>
          <Route path='/player/:user_id/:video_id/:timestamp' component={LoadVideo} />
          <Route path='/player/:user_id/:video_id' component={LoadVideo} />
          <Route path='/dashboard'>
            <Dashboard data={data} />
          </Route>
        </Switch>
      </Router>
    </div >
  );
}

export default App;
