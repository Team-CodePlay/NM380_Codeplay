import firebase from 'firebase';

const firebaseConfig = {
  apiKey: "AIzaSyAluCUOti4IFU4FkgaJMcmxpz1weWNgkZU",
  authDomain: "geoplay-codeplay.firebaseapp.com",
  databaseURL: "https://geoplay-codeplay.firebaseio.com",
  projectId: "geoplay-codeplay",
  storageBucket: "geoplay-codeplay.appspot.com",
  messagingSenderId: "712509088596",
  appId: "1:712509088596:web:1ceea24702d5abcb9a1ec1",
  measurementId: "G-YE7TK1CE3H"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);

export default firebase;