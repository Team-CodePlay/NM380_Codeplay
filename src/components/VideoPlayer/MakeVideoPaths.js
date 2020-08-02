/* global google */
import React, { useState, useEffect } from "react";
import { Polyline, lineSymbol } from "react-google-maps";
// import Card from 'react-bootstrap/Card';
// import Button from 'react-bootstrap/Button';
// import CardGroup from 'react-bootstrap/CardGroup';

// Calculates distance between lat lng
const haversine_distance = (mk1, mk2) => {
  var R = 3958.8; // Radius of the Earth in miles
  var rlat1 = mk1.lat * (Math.PI / 180); // Convert degrees to radians
  var rlat2 = mk2.lat * (Math.PI / 180); // Convert degrees to radians
  var difflat = rlat2 - rlat1; // Radian difference (latitudes)
  var difflon = (mk2.lng - mk1.lng) * (Math.PI / 180); // Radian difference (longitudes)

  var d =
    2 *
    R *
    Math.asin(
      Math.sqrt(
        Math.sin(difflat / 2) * Math.sin(difflat / 2) +
          Math.cos(rlat1) *
            Math.cos(rlat2) *
            Math.sin(difflon / 2) *
            Math.sin(difflon / 2)
      )
    );
  return d;
};

const MakeVideoPaths = (props) => {
  const [selectedPath, setSelectedPath] = useState(null);
  const [selectedPoint, setSelectedPoint] = useState(null);
  // const [dispAdd, setDispAdd] = useState([])
  // const [pathKey, setPathKey] = useState(0)

  // Path Id
  var pathKey = 0;

  const path = [];
  props.data.map((datapoint) => {
    path.push(datapoint);
  });

  return (
    <div>
      <Polyline
        path={path}
        key={"PathKey" + pathKey}
        id={"PathID" + pathKey}
        geodesic={true}
        options={{
          strokeColor: "BLUE",
          strokeOpacity: 0.8,
          strokeWeight: 8,
          icons: [
            {
              icon: lineSymbol,
              offset: "0",
              repeat: "20px",
            },
          ],
        }}
        onClick={(resp) => {
          // sets State
          setSelectedPath(path);

          // Clicked point cordinates
          var latlng = { lat: resp.latLng.lat(), lng: resp.latLng.lng() };

          // finds nearest point in data to the clicked point : can be optimized with better search algo
          let minDist = 99999999;
          let markPt = { lat: 0, lng: 0 };
          path.map((point) => {
            var dist = haversine_distance(latlng, point);
            if (dist < minDist) {
              minDist = dist;
              markPt = point;
            }
          });

          props.updateTimeFromMap(props.data[path.indexOf(markPt)].video_time);
          setSelectedPoint(markPt);
        }}
      />
    </div>
  );
};

export default MakeVideoPaths;
