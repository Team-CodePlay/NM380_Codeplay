import React from "react";
import "./App.css";
import MultiMap from "./components/MultiMap/MultiMap";
import Header from "./components/layouts/Header";
import data from "./data/dummy.json";

function App() {
  return (
    <div className="App">
      <Header />
      <MultiMap data = {data}/>
    </div>
  );
}

export default App;
