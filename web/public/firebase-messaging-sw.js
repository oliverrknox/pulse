importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js');

(async function () {
    const firebaseConfig = await fetch("/firebase-config.json")
        .then(response => response.json())
        .catch(error => {
            console.error('Error getting firebase config:', error);
        });

    firebase.initializeApp(firebaseConfig);
    const messaging = firebase.messaging();

    messaging.onBackgroundMessage((payload) => {
        console.log('Received background message: ', payload);

        // Customize the notification here
        const notificationTitle = payload.notification.title;
        const notificationOptions = {
            body: payload.notification.body,
            icon: payload.notification.icon
        };

        self.registration.showNotification(notificationTitle, notificationOptions);
    });
})()
