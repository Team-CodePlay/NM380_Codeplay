/* global google */
import React, { useState, useEffect } from "react";
import { Marker, InfoWindow, Polyline, lineSymbol } from "react-google-maps";
import {
  Card,
  Button,
  CardGroup,
  DropdownButton,
  Dropdown,
} from "react-bootstrap";

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
  const [selectedPoint, setselectedPoint] = useState();
  const [mapData, setMapData] = useState();
  const [selectedPath, setselectedPath] = useState();

  // @todo : Add more colors
  const color = ["blue", "green", "red", "yellow"];

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

    const listener = (e) => {
      if (e.key === "Escape") {
        setselectedPath(null);
        setselectedPoint(null);
      }
    };
    window.addEventListener("keydown", listener);
    return () => {
      window.removeEventListener("keydown", listener);
    };
  }, []);

  const createPathPoints = (geotags, start, end) => {
    var pathpoints = [];
    pathpoints.push(start);
    geotags.map((pt) => pathpoints.push({ lat: pt.lat, lng: pt.lng }));
    pathpoints.push(end);
    return pathpoints;
  };

  const pathClicked = (path) => {
    setselectedPath(path);
  };

  if (mapData !== undefined) {
    return (
      <div>
        {mapData.map((path, pathKey) => {
          var thisColor = color[pathKey % color.length];
          var pathpoints = createPathPoints(
            path.geotags,
            path.start_location,
            path.end_location
          );
          return (
            <div>
              <Polyline
                path={pathpoints}
                key={pathKey}
                geodesic={true}
                options={{
                  strokeColor: thisColor,
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
                  pathClicked(path);

                  var latlng = {
                    lat: resp.latLng.lat(),
                    lng: resp.latLng.lng(),
                  };

                  // finds nearest point in data to the clicked point : can be optimized with better search algo
                  let minDist = 99999999;
                  let markPt = { lat: 0, lng: 0 };
                  path.geotags.map((point) => {
                    var dist = haversine_distance(latlng, {
                      lat: point.lat,
                      lng: point.lng,
                    });
                    if (dist < minDist) {
                      minDist = dist;
                      markPt = point;
                    }
                  });

                  // sets the mearest point to plot marker
                  setselectedPoint(markPt);
                }}
              />
            </div>
          );
        })}

        {selectedPoint && (
          <InfoWindow
            onCloseClick={() => {
              setselectedPoint(null);
            }}
            position={{
              lat: selectedPoint.lat,
              lng: selectedPoint.lng,
            }}
          >
            <div>
              <p>
                <strong>Vehicle Speed : </strong>
                {selectedPoint.speed}
                <br />
                <strong>Video Timestamp : </strong>
                {selectedPoint.video_time / 1000} Seconds
                <br />
                <strong>Recoding Time : </strong>
                {Date(selectedPoint.timestamp * 1000)}
              </p>
            </div>
          </InfoWindow>
        )}

        {selectedPath && (
          <Polyline
            path={createPathPoints(
              selectedPath.geotags,
              selectedPath.start_location,
              selectedPath.end_location
            )}
            key="Highlighted Path"
            geodesic={true}
            options={{
              strokeColors: "black",
              strokeOpacity: 1,
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

              var latlng = {
                lat: resp.latLng.lat(),
                lng: resp.latLng.lng(),
              };

              // finds nearest point in data to the clicked point : can be optimized with better search algo
              let minDist = 99999999;
              let markPt = { lat: 0, lng: 0 };
              selectedPath.geotags.map((point) => {
                var dist = haversine_distance(latlng, {
                  lat: point.lat,
                  lng: point.lng,
                });
                if (dist < minDist) {
                  minDist = dist;
                  markPt = point;
                }
              });

              // sets the mearest point to plot marker
              setselectedPoint(markPt);
            }}
          />
        )}
      </div>
    );
  }
  return <div></div>;
};

export default MakeMultiPaths;
