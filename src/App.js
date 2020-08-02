import React, { useState, useEffect } from "react";
import "./App.css";
import MultiMap from "./components/MultiMap/MultiMap";
import Header from "./components/layouts/Header";
import Spinner from "./components/utils/Spinner";
import firebase from './components/utils/firebase';

function App() {
  const [data, setData] = useState();

  useEffect(() => {
    const fetchData = async () => {
      const videosRef = firebase
        .database()
        .ref("videos");
      const videosData = (await videosRef.once("value")).val();
      console.log("Firebase date:");
      console.log(videosData);
      setData(videosData);
    };
    fetchData();
  }, []);

  return (
    <div className="App">
      <Header />
      {data ? <MultiMap data={data} /> : <Spinner />}
    </div>
  );
}

export default App;
