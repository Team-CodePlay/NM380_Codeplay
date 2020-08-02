/* global google */
import React, { Component } from "react";
import { Player, ControlBar } from "video-react";
import { Container, Row, Col } from "react-bootstrap";
// import data from "./data/geotaggedvideo-1578585943197--MDEU91Mt2K8PXy1Y01M-export.json";
// import firebase from "./FirebaseData";

import {
  withGoogleMap,
  GoogleMap,
  Marker,
  withScriptjs,
} from "react-google-maps";
import MakeMarkers from "./MakeVideoMarkers";
import MakePaths from "./MakeVideoPaths";

const sources = {
  //   bunnyTrailer: "http://media.w3.org/2010/05/bunny/trailer.mp4",
  walkingVideo: "assets/video.mp4",
};

var flagToFitBound = 0;

export default class VideoPlayer extends Component {
  constructor(props, context) {
    super(props, context);
    console.log("Video data");
    console.log(this.props.data);
    this.state = {
      source: sources.walkingVideo,
    };

    this.play = this.play.bind(this);
    this.pause = this.pause.bind(this);
    this.load = this.load.bind(this);
    this.changeCurrentTime = this.changeCurrentTime.bind(this);
    this.seek = this.seek.bind(this);
    this.changePlaybackRateRate = this.changePlaybackRateRate.bind(this);
    this.changeVolume = this.changeVolume.bind(this);
    this.setMuted = this.setMuted.bind(this);
    this.markerPoints = this.getMarkerPoints();
    this.timeloc = this.getTimeLocation();
    this.MyMap = this.MyMap.bind(this);
    this.fitBounds = this.fitBounds.bind(this);
    this.updateTimeFromMap = this.updateTimeFromMap.bind(this);
    this.sendStartAdd = this.sendStartAdd.bind(this);
    this.sendEndAdd = this.sendEndAdd.bind(this);
    this.startAdd = this.startAdd;
    this.endAdd = this.endAdd;
  }

  componentDidMount() {
    // subscribe state change
    this.player.subscribeToStateChange(this.handleStateChange.bind(this));
  }

  setMuted(muted) {
    return () => {
      this.player.muted = muted;
    };
  }

  handleStateChange(state) {
    // copy player state to this component's state
    this.setState({
      player: state,
    });
  }

  play() {
    this.player.play();
  }

  pause() {
    this.player.pause();
  }

  load() {
    this.player.load();
  }

  changeCurrentTime(seconds) {
    return () => {
      const { player } = this.player.getState();
      this.player.seek(player.currentTime + seconds);
    };
  }

  seek(seconds) {
    return () => {
      this.player.seek(seconds);
    };
  }

  changePlaybackRateRate(steps) {
    return () => {
      const { player } = this.player.getState();
      this.player.playbackRate = player.playbackRate + steps;
    };
  }

  changeVolume(steps) {
    return () => {
      const { player } = this.player.getState();
      this.player.volume = player.volume + steps;
    };
  }

  changeSource(name) {
    return () => {
      this.setState({
        source: sources[name],
      });
      this.player.load();
    };
  }

  // MAP FUNCTIONS START

  // Gets all Start and end point of paths to place markers on
  getMarkerPoints = () => {
    const markerPoints = [];
    markerPoints.push(this.props.data.start_location);
    markerPoints.push(this.props.data.end_location);
    return markerPoints;
  };

  // Update Time From Map
  updateTimeFromMap = (time) => {
    time = Math.floor(time / 1000);
    this.player.seek(time);
  };

  sendStartAdd = (add) => {
    this.startAdd = add;
  };

  sendEndAdd = (add) => {
    this.endAdd = add;
  };

  // Returns time : loc array
  getTimeLocation = () => {
    const timeloc = [];

    // Add 1 point per second
    this.props.data.geotags.map((dpoints) => {
      timeloc[Math.floor(dpoints.video_time / 1000)] = dpoints;
    });

    return timeloc;
  };

  // Adjusts zoom to fit both enpoint markers uses global flag flagToFitBound @ SANKET
  fitBounds = (map) => {
    if (map == null) return "";
    flagToFitBound = 1;
    const bounds = new window.google.maps.LatLngBounds();
    this.markerPoints.map((pt) => {
      bounds.extend(pt);
      return "";
    });
    map.fitBounds(bounds);
  };

  // Builds Map Component
  MyMap = withScriptjs(
    withGoogleMap(() => {

      const fetchPosition = () => {

        if (Math.floor(this.state.player.currentTime) > 0) {
          if (
            this.timeloc[Math.floor(this.state.player.currentTime)] ===
            undefined
          ) {
            let prev = this.timeloc[
              Math.floor(this.state.player.currentTime - 1)
            ];

            let check = 1;

            while(Math.floor(this.state.player.currentTime + check) === undefined ){
                check +=1
            }
            let next = this.timeloc[
              Math.floor(this.state.player.currentTime + check)
            ];

            // Time in video exceeds data collected time
            if ((prev === undefined) | (next === undefined)) {
              return null;
            }

            // return average loction if time data doesnt exist
            return {
              lat: (prev.lat + next.lat) / 2,
              lng: (prev.lng + next.lng) / 2,
            };
          }
          // return data
          return this.timeloc[Math.floor(this.state.player.currentTime)];
        }
        return null;
      };

      // if no data is found
      if (this.markerPoints[0] == undefined) {
        return <div></div>;
      }

      return (
        <GoogleMap
          defaultZoom={15}
          defaultCenter={this.markerPoints[0]}
          ref={(map) => {
            if ((map != null) & (flagToFitBound === 0)) {
              this.fitBounds(map);
            }
          }}
        >
          <MakeMarkers markerPoints={this.markerPoints} parent="VideoPlayer" />

          <MakePaths
            data={this.props.data.geotags}
            updateTimeFromMap={this.updateTimeFromMap}
            time={this.state.player}
            parent="VideoPlayer"
          />

          {/* GEO LOCTION MARKER */}
          <Marker
            position={fetchPosition()}
            icon={{
              url: "https://img.icons8.com/doodle/48/000000/street-view.png",
            }}
          />
        </GoogleMap>
      );
    })
  );

  // MAP FUNCTIONS END

  render() {
    return (
      <div>
        <Container fluid className="rootLayout">
          <Row>
            <Col lg={6}>
              <Player
                ref={(player) => {
                  this.player = player;
                }}
                fluid={false}
                height={650}
                width={750}
              >
                <source src={this.state.source} />
                <ControlBar autoHide={false} />
              </Player>
            </Col>
            <Col lg={6}>
              <this.MyMap
                googleMapURL={`https://maps.googleapis.com/maps/api/js?key=${process.env.REACT_APP_GOOGLE_MAPS_OLD_API_KEY}&v=3.exp&libraries=geometry,drawing,places`}
                loadingElement={<div style={{ height: `100%` }} />}
                containerElement={<div style={{ height: `100%` }} />}
                mapElement={<div style={{ height: `100%` }} />}
              />
            </Col>
          </Row>
        </Container>
      </div>
    );
  }
}
