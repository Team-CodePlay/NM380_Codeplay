/* global google */
import React, { useState, useEffect } from "react";
import { Marker, InfoWindow } from "react-google-maps";

export default function MakeMarkers(props) {
  const [selectedPath, setselectedPath] = useState();
  const [selectedMarker, setselectedMarker] = useState();
  const [mapData, setMapData] = useState();

  const startEndChoice = ["S","E"]

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

  if (props.parent === "VideoPlayer") {
    return (
      //Draw Marker Points
      <div>
        {props.markerPoints.map((point, markerId) => {
          return (
            <Marker
              key={markerId}
              position={point}
              // icon={{
              //     url:'https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=' + Math.floor((markerId++)/2) +'|FE6256|000000'
              // }}
            />
          );
        })}
      </div>
    );
  }

  if (props.parent === "MultiMap") {
    return (
      //Draw Marker Points
      <div>
        {props.markerPoints.map((point, markerId) => {
          return (
            <Marker
              key={markerId}
              position={point}
              animation = {google.maps.Animation.DROP}
              label={startEndChoice[markerId%2] + Math.floor(markerId / 2)}
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
                {selectedPath.videoname}<br/>
                <strong>UserName : </strong>
                {selectedPath.username}<br/>
                <strong>Record Date : </strong>
                {Date(selectedPath.video * 1000)}
              </p>
            </div>
          </InfoWindow>
        )}
      </div>
    );
  }
}
