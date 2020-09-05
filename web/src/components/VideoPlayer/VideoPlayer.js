/* global google */
import React, { Component } from "react";
import { Player, ControlBar } from "video-react";
import { Container, Row, Col, ListGroup } from "react-bootstrap";
import firebase from "../utils/firebase";

import {
  withGoogleMap,
  GoogleMap,
  Marker,
  withScriptjs,
} from "react-google-maps";
import MakeMarkers from "./MakeVideoMarkers";
import MakePaths from "./MakeVideoPaths";
import axios from "axios";

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
      source: "",
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
    this.fetchLabels = this.fetchLabels.bind(this);

    this.playerMarker = null;
    this.playerBearing = null;
    this.playerSpeed = null;
    this.label = null;

    this.extractLabel = this.extractLabel.bind(this)
  }

  async loadVideo() {
    const storage = firebase.storage();
    const videoReference = storage.refFromURL(this.props.data.video_path);
    const videoStreamingURL = await videoReference.getDownloadURL();
    console.log(videoStreamingURL);
    this.setState({
      source: videoStreamingURL,
    });
    this.load();
  }

  componentDidMount() {
    // subscribe state change
    this.player.subscribeToStateChange(this.handleStateChange.bind(this));
    this.loadVideo();
    this.player.seek(this.props.timestamp);
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

  fetchLabels = () => {
    let url = new URL(window.location.href);
    url =
      `http://127.0.0.1:5000/?db_path=videos` +
      url.pathname.substring(7, url.pathname.length);
    
    console.log(url)

    axios.get(url, {
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers":
          "Origin, X-Requested-With, Content-Type, Accept",
        "Content-Type": "application/json",
        Authorisation: null,
      },
    })
    .then(()=> window.location.reload())
  };


  extractLabel = data =>{
    let res= ""
    data.map(lbl=>{
      console.log(lbl)
      res = res + lbl.split(':')[0] +","
    })
    return res;
  }  

  // Builds Map Component
  MyMap = withScriptjs(
    withGoogleMap(() => {
      // Marker Position
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

            while (
              Math.floor(this.state.player.currentTime + check) === undefined
            ) {
              check += 1;
            }
            let next = this.timeloc[
              Math.floor(this.state.player.currentTime + check)
            ];

            // Time in video exceeds data collected time
            if ((prev === undefined) | (next === undefined)) {
              return;
            }

            // return average loction if time data doesnt exist
            return {
              lat: (prev.lat + next.lat) / 2,
              lng: (prev.lng + next.lng) / 2,
            };
          }
          // return data
          return {
            lat: this.timeloc[Math.floor(this.state.player.currentTime)].lat,
            lng: this.timeloc[Math.floor(this.state.player.currentTime)].lng,
          };
        }
        return;
      };

      // if no data is found
      if (this.markerPoints[0] === undefined) {
        return <div></div>;
      }

      // get marker bearing
      const fetchBearing = () => {
        if (Math.floor(this.state.player.currentTime) > 0) {
          if (
            this.timeloc[Math.floor(this.state.player.currentTime)] ===
            undefined
          ) {
            let check = 1;

            while (
              Math.floor(this.state.player.currentTime - check) === undefined
            ) {
              check += 1;
            }
            if (
              (Math.floor(this.state.player.currentTime - check) > 0) &
              (this.timeloc[
                Math.floor(this.state.player.currentTime) - check
              ] !==
                undefined)
            ) {
              return parseInt(
                this.timeloc[Math.floor(this.state.player.currentTime) - check]
                  .bearing
              );
            }
            return parseInt(0);
          }
          return parseInt(
            this.timeloc[Math.floor(this.state.player.currentTime)].bearing
          );
        }
        return parseInt(0);
      };

      const fetchSpeed = () => {
        if (Math.floor(this.state.player.currentTime) > 0) {
          if (
            this.timeloc[Math.floor(this.state.player.currentTime)] ===
            undefined
          ) {
            let check = 1;

            while (
              Math.floor(this.state.player.currentTime - check) === undefined
            ) {
              check += 1;
            }
            if (
              (Math.floor(this.state.player.currentTime - check) > 0) &
              (this.timeloc[
                Math.floor(this.state.player.currentTime) - check
              ] !==
                undefined)
            ) {
              return parseInt(
                this.timeloc[Math.floor(this.state.player.currentTime) - check]
                  .speed
              );
            }
            return parseInt(0);
          }
          return parseInt(
            this.timeloc[Math.floor(this.state.player.currentTime)].speed
          );
        }
        return parseInt(0);
      };


      // const fetchLabel = () => {

      //   // console.log(this.timeloc[Math.floor(this.state.player.currentTime)].label)
      //   if (Math.floor(this.state.player.currentTime) > 0) {
      //     if (
      //       this.timeloc[Math.floor(this.state.player.currentTime)] ===
      //       undefined
      //     ) {return ;} 

      //     if(this.timeloc[Math.floor(this.state.player.currentTime)].label !== undefined){
      //       let res = ""
      //       this.timeloc[Math.floor(this.state.player.currentTime)].label.map(pt=>{
      //         console.log(pt)
      //         res = res + (pt["label"]) +", "
      //       })
      //       return res;
      //   }else{ return ;}
          
      //   }
      //   return ;
      // };

          

      // Function to return Location Marker
      const markerLoc = () => {
        this.playerMarker = fetchPosition();
        this.playerBearing = fetchBearing();
        this.playerSpeed = fetchSpeed();
        return (
          <div>
            <Marker
              position={this.playerMarker}
              icon={{
                path: window.google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
                scale: 6,
                fillColor: "red",
                fillOpacity: 0.8,
                strokeWeight: 2,
                rotation: this.playerBearing,
              }}
            />
          </div>
        );
      };

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

          {markerLoc()}
        </GoogleMap>
      );
    })
  );

  // MAP FUNCTIONS END

  render() {
    return (
      <div>
        {this.playerMarker && (
          <ListGroup horizontal style={{ marginLeft: "1%" }}>
            <ListGroup.Item variant="success">Location</ListGroup.Item>
            <ListGroup.Item>
              Lat:{JSON.stringify(this.playerMarker.lat)} Lng:
              {JSON.stringify(this.playerMarker.lng)}
            </ListGroup.Item>
            <ListGroup.Item variant="success">Bearing</ListGroup.Item>
            <ListGroup.Item>
              {JSON.stringify(this.playerBearing)}
            </ListGroup.Item>
            <ListGroup.Item variant="success">Speed</ListGroup.Item>
            <ListGroup.Item>
              {JSON.stringify(this.playerSpeed)} km/hr
            </ListGroup.Item>

            <ListGroup.Item
              action
              variant="primary"
              onClick={this.fetchLabels}
              style={{ width: "auto", borderRadius: "0.5rem" }}
            >
              <i className="fa fa-eye"></i> Process this Video
            </ListGroup.Item>
          </ListGroup>
        )}

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
              {this.state.player && (
                <this.MyMap
                  googleMapURL={`https://maps.googleapis.com/maps/api/js?key=${process.env.REACT_APP_GOOGLE_MAPS_API_KEY}&v=3.exp&libraries=geometry,drawing,places`}
                  loadingElement={<div style={{ height: `100%` }} />}
                  containerElement={<div style={{ height: `100%` }} />}
                  mapElement={<div style={{ height: `100%` }} />}
                />
              )}
            </Col>
          </Row>
        </Container>
        <Container>
        <ListGroup.Item variant="success">Top Labels</ListGroup.Item>

  {this.props.data.labels &&
  <ListGroup.Item>
    {this.extractLabel(this.props.data.labels)}
  </ListGroup.Item>
}
        </Container>
      </div>
    );
  }
}
