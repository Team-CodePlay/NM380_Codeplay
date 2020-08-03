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

const CardsView = (props) => {
  const [mapData, setMapData] = useState();

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
  }, []);

  const createPathPoints = (geotags, start, end) => {
    var pathpoints = [];
    pathpoints.push(start);
    geotags.map((pt) => pathpoints.push({ lat: pt.lat, lng: pt.lng }));
    pathpoints.push(end);
    return pathpoints;
  };

  // fitBounds zoom
  const seePath = (path) => {
    props.zoomOnPath(
      createPathPoints(path.geotags, path.start_location, path.end_location)
    );
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
    const cardsInOneRow = 4;
    let cardsArray = [];
    let count = 0;
    for (let i = 0; i < mapData.length; i++)
      cardsArray[i] = [];
    return (
      <div>
        {mapData.map((path, pathKey) => {
          cardsArray[Math.floor(count++ / cardsInOneRow)].push(
            <Card
              key={pathKey}
              border="primary"
              style={{
                margin: "10px",
                width: "18rem",
                borderLeft: "1px solid",
                borderRadius: "0.5rem",
              }}
            >
              <Card.Body>
                <Card.Title>Path{path.videoname}</Card.Title>
                <Card.Text>
                  <strong>User :</strong> {path.username}
                  <br />
                  <strong>Data Collection Time :</strong>{" "}
                  {new Date(path.upload_timestamp).toLocaleString("en-GB")}
                  <br />
                  <strong>Video Duration :</strong> {path.duration}
                  {/* <br />
                  <strong>Video Link :</strong> {path.video_path} */}
                </Card.Text>

                <Button
                  style={{ margin: "0.25rem" }}
                  onClick={() => {
                    seePath(path);
                    window.scrollTo({
                      top: 0,
                      behavior: "smooth",
                    });
                  }}
                  variant="primary"
                >
                  See Path
                </Button>

                <Button style={{ margin: "0.25rem" }} variant="primary">
                  <Link
                    to={`/player/${path.username}/${path.videoname}`}
                    style={{ color: "white" }}
                  >
                    Watch Video
                  </Link>
                </Button>
                <DropdownButton
                  style={{ margin: "0.25rem" }}
                  variant="success"
                  title="Export To KML"
                >
                  <Dropdown.Item
                    id={"view" + pathKey}
                    variant="light"
                    onClick={() => exportToKml(path, "view")}
                  >
                    View KML
                  </Dropdown.Item>
                  <Dropdown.Item
                    id={"download" + pathKey}
                    variant="light"
                    onClick={() => exportToKml(path, "download")}
                  >
                    Download KML
                  </Dropdown.Item>
                </DropdownButton>
              </Card.Body>
            </Card>
          );
        })}
        {cardsArray.map((cardRow) => <CardGroup>{cardRow}</CardGroup>)}
      </div>
    );
  }
  return <div></div>;
};

export default CardsView;
