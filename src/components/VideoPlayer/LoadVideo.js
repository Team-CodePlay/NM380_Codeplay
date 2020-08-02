import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import VideoPlayer from './VideoPlayer';
import Spinner from '../utils/Spinner';
import firebase from '../utils/firebase';

const LoadVideo = props => {
    const [videoData, setVideoData] = useState(null);
    let params = useParams();
    useEffect(() => {
        const fetchData = async () => {
            const videosRef = firebase
                .database()
                .ref(`videos/${params.user_id}/${params.video_id}`);
            const videosData = (await videosRef.once("value")).val();
            console.log(`videos/${params.user_id}/${params.video_id}`);
            console.log("Firebase video data:");
            console.log(videosData);
            setVideoData(videosData);
        };
        fetchData();
    }, []);

    return (
        <>
            {videoData === null ?
                <Spinner /> :
                <>
                    {params.timestamp ? <VideoPlayer timestamp={params.timestamp} data={videoData} /> : <VideoPlayer notime="asdf" data={videoData} />}
                </>
            }
        </>
    );
};

export default LoadVideo;
