import React from "react";
import "./App.css";
import MultiMap from "./components/MultiMap/MultiMap";
import Header from "./components/MultiMap/layouts/Header";

function App() {
  return (
    <div className="App">
      <Header />
      <MultiMap />
    </div>
  );
}

export default App;
