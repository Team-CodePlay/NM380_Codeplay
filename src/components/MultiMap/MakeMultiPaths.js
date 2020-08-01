/* global google */
import React, { useState, useEffect } from "react";
import { Polyline, lineSymbol } from "react-google-maps";

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

const MakeMultiPaths = (props) => {
  //   const [selectedMarker, setselectedMarker] = useState();
  const [mapData, setMapData] = useState();
  //   const [selectedPath, setselectedPath] = useState();

  // @todo : Add more colors
  const color = ["blue", "green", "red", "yellow"];
  let clr = 0;

  // Path Id
  var pathKey = 0;

  useEffect(() => {
    var temp = [];
    // Iterate over Data to build mapData
    Object.keys(props.data).forEach((user) => {
      Object.keys(props.data[user]).forEach((video) => {
        props.data[user][video]["username"] = user;
        props.data[user][video]["videoname"] = video;
        temp.push(props.data[user][video]);
      });
      setMapData(temp);
    });
  }, []);

  //   useEffect(() => {
  //     const listener = (e) => {
  //       if (e.key === "Escape") {
  //         setselectedMarker(null);
  //         setselectedPath(null);
  //       }
  //     };
  //     window.addEventListener("keydown", listener);
  //     return () => {
  //       window.removeEventListener("keydown", listener);
  //     };
  //   }, []);

  const createPathPoints = (geotags, start, end) => {
    var pathpoints = [];
    pathpoints.push(start);
    geotags.map((pt) => pathpoints.push({ lat: pt.lat, lng: pt.lng }));
    pathpoints.push(end);
    return pathpoints;
  };

  if (mapData !== undefined) {
    return (
      <div>
        {mapData.map((path, pathKey) => {
          var thisColor = color[clr++ % color.length];
          var pathpoints = createPathPoints(
            path.geotags,
            path.start_location,
            path.end_location
          ); 
          return (
            <Polyline
              path={pathpoints}
              key={pathKey}
              geodesic={true}
              options={{
                strokeColor: thisColor,
                strokeOpacity: 0.6,
                strokeWeight: 8,
                icons: [
                  {
                    icon: lineSymbol,
                    offset: "0",
                    repeat: "20px",
                  },
                ],
              }}
            />
          );
        })}
      </div>
    );


  }
  return <div></div>;
};

export default MakeMultiPaths;
