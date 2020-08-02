/* global google */
import React, { useState, useEffect } from "react";
import { Marker, InfoWindow } from "react-google-maps";
import { Button } from "react-bootstrap";
import { Link } from "react-router-dom";
import { kmlStart1, kmlStart2, kmlEnd } from "./kmlUtil";

export default function MakeMarkers(props) {
  const [selectedPath, setselectedPath] = useState();
  const [selectedMarker, setselectedMarker] = useState();
  const [mapData, setMapData] = useState();

  const startEndChoice = ["A", "B"];

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

  useEffect(() => {
    const listener = (e) => {
      if (e.key === "Escape") {
        setselectedMarker(null);
        setselectedPath(null);
      }
    };
    window.addEventListener("keydown", listener);
    return () => {
      window.removeEventListener("keydown", listener);
    };
  }, []);

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

  if (props.parent === "MultiMap") {
    return (
      //Draw Marker Points
      <div>
        {props.markerPoints.map((point, markerId) => {
          return (
            <Marker
              key={markerId}
              position={point}
              animation={google.maps.Animation.DROP}
              label={startEndChoice[markerId % 2] + Math.floor(markerId / 2)}
              // icon={{
              //   url:
              //     `https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=${startEndChoice[markerId%2]}${Math.floor(markerId / 2)}|FE6256|000000`,
              // }}
              onClick={() => {
                setselectedPath(mapData[Math.floor(markerId++ / 2)]);
                setselectedMarker(point);
              }}
            />
          );
        })}

        {selectedPath && selectedMarker && (
          <InfoWindow
            onCloseClick={() => {
              setselectedPath(null);
              setselectedMarker(null);
            }}
            position={{
              lat: selectedMarker.lat + 0.0001,
              lng: selectedMarker.lng,
            }}
          >
            <div>
              <h5>Marker Point</h5>
              <p>
                <strong>Path : </strong>
                {selectedPath.videoname}
                <br />
                <strong>UserName : </strong>
                {selectedPath.username}
                <br />
                <strong>Record Date : </strong>
                {new Date(selectedPath.video_start_time).toLocaleString(
                  "en-GB"
                )}
              </p>
              <Button>
                <Link
                  to={`/player/${selectedPath.username}/${selectedPath.videoname}/`}
                  style={{ color: "black" }}
                >
                  Watch Video
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
      </div>
    );
  }
}
