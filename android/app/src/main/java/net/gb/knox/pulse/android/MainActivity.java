package net.gb.knox.pulse.android;

import android.os.Bundle;
import android.widget.Button;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import net.gb.knox.pulse.android.service.MessageService;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
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
        subscribeButton.setOnClickListener(v -> sendTokenToServer(MessageService.getToken()));

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void sendTokenToServer(String token) {
        Log.d(TAG, "Sending token to server...");
        executorService.execute(() -> {
            try {
                URL url = new URL("https://10.0.2.2:8080/subscribe"); // Use the appropriate URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                String jsonInputString = "{\"registrationToken\": \"" + token + "\"}";

                Log.d(TAG, "Request body: " + jsonInputString);


                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending token to server", e);
            }
        });
    }


}