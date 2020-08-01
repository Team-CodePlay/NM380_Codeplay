import React, { useState } from 'react';
import {
    withGoogleMap,
    GoogleMap,
    withScriptjs,
} from "react-google-maps";
import data from '../../data/dummy.json';

const MultiMap = () => {

    console.log(data);

    Object.keys(data).forEach(user => {
        Object.keys(data[user]).forEach(video => {
            console.log(user, video);
        });
    });

    const MapWithPaths = () => {
        return (
            <GoogleMap
                defaultZoom={15}
                defaultCenter={{ lat: 19.23, lng: 72.85 }}
            ></GoogleMap>
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