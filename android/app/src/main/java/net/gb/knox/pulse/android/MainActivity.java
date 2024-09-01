package net.gb.knox.pulse.android;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import net.gb.knox.pulse.android.service.MessageService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button subscribeButton = findViewById(R.id.subscribe);
        subscribeButton.setOnClickListener(v -> subscribe(MessageService.getToken()));

        Button unsubscribeButton = findViewById(R.id.unsubscribe);
        unsubscribeButton.setOnClickListener(v -> unsubscribe(MessageService.getToken()));

        Button subscribeToTopicButton = findViewById(R.id.subscribe_to_topic);
        subscribeToTopicButton.setOnClickListener(v -> subscribeToTopic(MessageService.getToken()));

        Button unsubscribeFromTopicButton = findViewById(R.id.unsubscribe_from_topic);
        unsubscribeFromTopicButton.setOnClickListener(v -> unsubscribeFromTopic(MessageService.getToken()));

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Failed to fetch FCM token");
                return;
            }

            String token = task.getResult();
            MessageService.setToken(token);
            Log.d(TAG, "FCM token: " + token);
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void subscribe(String token) {
        Log.d(TAG, "Subscribing to server...");
        sendRequest("POST", "https://10.0.2.2:8080/subscribe", (conn -> {
            String jsonInputString = "{\"registrationToken\": \"" + token + "\"}";
            Log.d(TAG, "Request body: " + jsonInputString);

            OutputStream os = conn.getOutputStream();
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }));
    }

    private void unsubscribe(String token) {
        Log.d(TAG, "Unsubscribe to server...");
        sendRequest("DELETE", "https://10.0.2.2:8080/unsubscribe/" + token);
    }

    private void subscribeToTopic(String token) {
        Log.d(TAG, "Subscribing to topic on server...");
        sendRequest("POST", "https://10.0.2.2:8080/subscribe/topic/pulse", (conn -> {
            String jsonInputString = "{\"registrationToken\": \"" + token + "\"}";
            Log.d(TAG, "Request body: " + jsonInputString);

            OutputStream os = conn.getOutputStream();
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }));
    }

    private void unsubscribeFromTopic(String token) {
        Log.d(TAG, "Unsubscribe from topic on server...");
        sendRequest("DELETE", "https://10.0.2.2:8080/unsubscribe/" + token + "/topic/pulse");
    }

    private void sendRequest(String method, String path) {
        sendRequest(method, path, (conn) -> {});
    }

    @FunctionalInterface
    public interface RequestBody {
        void apply(HttpURLConnection connection) throws IOException;
    }

    private void sendRequest(String method, String path, RequestBody requestBody) {
        executorService.execute(() -> {
            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                requestBody.apply(conn);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending token to server", e);
            }
        });
    }

}