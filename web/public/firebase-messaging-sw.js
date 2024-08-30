importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js');

const firebaseConfig = await fetch("/firebase-config.json")
    .then(response => response.json())
    .catch(error => {
        console.error('Error getting firebase config:', error);
    });

firebase.initializeApp(firebaseConfig);
firebase.messaging();