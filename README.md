# NM380: App for recording and playing geotagged videos
Build Status: ![Android CI](https://github.com/stacks13/NM380_Codeplay/workflows/Android%20CI/badge.svg?branch=develop-android)

## Team Codeplay (ID: 2897)

## Members:
- Sahil Nirkhe (https://github.com/stacks13) - Team Leader
- Sanket Dalvi (https://github.com/sank8dalvi)
- Ritika Bhole (https://github.com/RitikaBhole)
- Manas Acharya (https://github.com/hod101s)
- Jaineel Mamtora (https://github.com/Jaineel-Mamtora)
- Daniel Lobo (https://github.com/danlobo1999)

## Problem Statement
Develop a mobile application for recording and playing geotagged videos.    
Unlike photos in which geotag data is of a single point and orientation pair, for videos geotag data is a sequence of point and orientation pairs.    
The mobile application should have two views. In one view the recorded video should play while simultaneously plotting field-of-view (orientation) cone and marker on an interactive map in the other view in a synchronized manner.    
The position shown on the map should match the play position of the video. The geotagged locations should be exportable in KML format with time tag information.    
Video : https://vedas.sac.gov.in/vcms/static/SIH-2020/VID-20191229-WA0174.mp4

## Features
- Cloud upload (database and storage)
- WSM url (Bing, OSM) supported alongwith custom url support for WSM
- Regional language support (Marathi, Hindi, Gujrati)
- Automatic Video labelling using Machie Learning
- Video Playback with Map 
    - As discussed, location marker rotates as per bearing
    - Marker related changes done as discussed
- Geotags encoded into video metadata (to preserve the location data)
- Filterable search view in video library.
    -As discussed, latitude, longitude, speed, bearing, dateTime is displayed on recording videos and on playback.
- Dark mode implemented (Saves power)

### Features under implementation
- Resumable upload to cloud while offline.
- First time tutorial for new users
- Video size control (bitrate),  custom file store location, power saving settings , upload settings
- Export to KML in app. (Web app implemented)


## Android App Screenshots
![image](https://user-images.githubusercontent.com/40513848/89127264-ced97080-d509-11ea-8f67-37de034265fe.png)    ![image](https://user-images.githubusercontent.com/40513848/89127323-617a0f80-d50a-11ea-8617-a861bda03d06.png)    
![image](https://user-images.githubusercontent.com/40513848/89127389-b28a0380-d50a-11ea-8620-a49da593eaf5.png)    ![image](https://user-images.githubusercontent.com/40513848/89127425-f3821800-d50a-11ea-807f-c0688dc92b25.png)
![image](https://user-images.githubusercontent.com/40513848/89127513-99358700-d50b-11ea-9125-7aa962524836.png)

## Web App Screenshots
![image](https://user-images.githubusercontent.com/40513848/89165431-2cb39a00-d596-11ea-8072-1c9e857cfcfe.png)    

![image](https://user-images.githubusercontent.com/40513848/89165844-c5e2b080-d596-11ea-901f-19bfbf1b3d21.png)

## Applications in: 
- Data collection for agriculture, disaster management, MNREGA  etc.
- and more...

## You can find the latest apk in the [Actions Tab](https://github.com/stacks13/NM380_Codeplay/actions)

