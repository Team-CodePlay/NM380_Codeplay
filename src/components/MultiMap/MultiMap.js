/* global google */
import React, { useState, useEffect } from "react";
import { withGoogleMap, GoogleMap, withScriptjs } from "react-google-maps";
import data from "../../data/dummy.json";
import MakeMarkers from "../utils/MakeMarkers";

var flagToFitBound = 0;

const MultiMap = (props) => {
  const [rawData, setRawData] = useState(props.data);
  //   const [mapData, setMapData] = useState();
  const [markerPoints, setMarkerPoints] = useState();

  useEffect(() => {
    // var temp = [];
    // // Iterate over Data to build mapData
    // Object.keys(rawData).forEach((user) => {
    //   Object.keys(data[user]).forEach((video) => {
    //     data[user][video]["username"] = user;
    //     data[user][video]["videoname"] = video;
    //     temp.push(data[user][video]);
    //   });
    //   setMapData(temp);
    // });

    // Gets all Start and end point of paths to place markers on
    const tempMarkerPoints = [];

    Object.keys(rawData).forEach((user) => {
      Object.keys(data[user]).forEach((video) => {
        tempMarkerPoints.push(data[user][video].start_location);
        tempMarkerPoints.push(data[user][video].end_location);
      });
      setMarkerPoints(tempMarkerPoints);
    });
  }, []);

  console.log("markerPoints" + markerPoints);

  // Create Map Bounds makes sure all points in markerPoints are visible in map ie sets zoom accordingly
  const fitBounds = (map) => {
    flagToFitBound = 1;
    const bounds = new window.google.maps.LatLngBounds();
    markerPoints.map((pt) => {
      bounds.extend(pt);
    });
    console.log(markerPoints);
    map.fitBounds(bounds);
  };

  const MapWithPaths = () => {
    return (
      <GoogleMap
        ref={(map) => {
          console.log("Map " + map);
          if (
            (map != null) &
            (flagToFitBound === 0) &
            (markerPoints !== undefined)
          ) {
            fitBounds(map);
            flagToFitBound = 1;
          }
        }}
        defaultZoom={15}
        // defaultCenter={{ lat: 14, lng: 71 }}
      >
        <MakeMarkers markerPoints={markerPoints} parent="MultiMap" />
      </GoogleMap>
    );
  };

  const MyMap = withScriptjs(withGoogleMap(MapWithPaths));

  return (
    <MyMap
      googleMapURL={`https://maps.googleapis.com/maps/api/js?key=${process.env.REACT_APP_GOOGLE_MAPS_OLD_API_KEY}&v=3.exp&libraries=geometry,drawing,places`}
      loadingElement={<div style={{ height: `100%` }} />}
      containerElement={<div style={{ height: `60vh` }} />}
      mapElement={<div style={{ height: `100%` }} />}
    />
  );
};

export default MultiMap;
