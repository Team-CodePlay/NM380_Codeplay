/* global google */
import React, { useState, useEffect } from "react";
import { InfoWindow, Polyline, lineSymbol } from "react-google-maps";
import {
  Card,
  Button,
  CardGroup,
  DropdownButton,
  Dropdown,
} from "react-bootstrap";
import { Link } from "react-router-dom";
import { kmlStart1, kmlStart2, kmlEnd } from "../utils/kmlUtil";

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
  const [videoLink, setvideoLink] = useState();

  // @todo : Add more colors
  const color = ["#ff0000", "#2cff00", "blue", "yellow"];

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
        setvideoLink(null);
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

  const exportToKml = (path, action) => {
    var kmlData = kmlStart1 + path.videoname + kmlStart2;

    path.geotags.map((pt) => {
      kmlData += `
        <Placemark>
            <styleUrl>#hiker-icon</styleUrl>
            <TimeStamp>1595088201781</TimeStamp>
            <Point>
                <coordinates>${pt.lng},${pt.lat}</coordinates>
            </Point>
        </Placemark>
        `;
    });

    kmlData += kmlEnd;

    const element = document.createElement("a");
    const file = new Blob([kmlData], { type: "text/kml" });
    element.href = URL.createObjectURL(file);
    if (action === "download") {
      element.download = `${path.videoname}.kml`;
    } else if (action === "view") {
      element.target = "_blank";
    }
    document.body.appendChild(element); // Mozilla
    element.click();
  };

  if (mapData !== undefined) {
    let cardsArray = [];
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
                setvideoLink(path.video_path);
                setselectedPoint(markPt);
              }}
            />
          );
        })}

        {selectedPoint && selectedPath && (
          <InfoWindow
            onCloseClick={() => {
              setselectedPoint(null);
              setvideoLink(null);
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
                {new Date(selectedPoint.timestamp).toLocaleString("en-GB")}
                {/* <br />
                <strong>Video Link : </strong>
                {videoLink} */}
              </p>
              <Button>

                <Link
                  to={`/player/${selectedPath.username}/${selectedPath.videoname}/${selectedPoint.video_time / 1000}`}
                  style={{ color: "black" }}
                >
                  Watch Video From Here
                </Link>
              </Button>{" "}
              <Button
                id="viewInWindow"
                variant="light"
                onClick={() => exportToKml(selectedPath, "view")}
              >
                View KML
              </Button>{" "}
              <Button
                id="downloadInWindow"
                variant="light"
                onClick={() => exportToKml(selectedPath, "download")}
              >
                Download KML
              </Button>{" "}
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
