package io.oliverknox.pulse.android.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.util.Log;

public class MessageService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private static String registrationToken;

    public static void setToken(String token) {
        registrationToken = token;
    }

    public static String getToken() {
        return registrationToken;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle FCM messages here
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        // Send token to your server (if necessary)
        registrationToken = token;
    }
}
