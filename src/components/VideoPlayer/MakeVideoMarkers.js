// Plots start and end Marker

/* global google */
import React, { useState } from "react";
import { Marker,MarkerClusterer } from "react-google-maps";

export default function MakeVideoMarkers(props) {
  const [selectedMarker, setselectedMarker] = useState("");

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
              icon={{
                url: 'https://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=' + Math.floor((markerId++) / 2) + '|FE6256|000000'
              }}
            />
          );
        })}
      </div>
    );
  }

}
