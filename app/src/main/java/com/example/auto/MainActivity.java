package com.example.auto;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button brakeButton, vibrationButton, airPressureButton, temperatureButton, humidityButton, exhaustButton;
    private Handler handler = new Handler();
    private RequestQueue requestQueue;
    private final String URL = "http://192.168.43.60/get_sensor_data1.php"; // Updated URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brakeButton = findViewById(R.id.brakeButton);
        vibrationButton = findViewById(R.id.vibrationButton);
        airPressureButton = findViewById(R.id.airPressureButton);
        temperatureButton = findViewById(R.id.temperatureButton);
        humidityButton = findViewById(R.id.humidityButton);
        exhaustButton = findViewById(R.id.exhaustButton);

        requestQueue = Volley.newRequestQueue(this);

        fetchLastSensorData();
    }

    private void fetchLastSensorData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Update each button based on sensor data
                            updateButton(response, "brake", brakeButton, 15, 40);
                            updateButton(response, "vibration", vibrationButton, 5, 20);
                            updateButton(response, "air_pressure", airPressureButton, 20, 50);
                            updateButton(response, "temperature", temperatureButton, 25, 50);
                            updateButton(response, "humidity", humidityButton, 30, 60);
                            updateButton(response, "exhaust", exhaustButton, 10, 30);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "JSON error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set a custom retry policy with increased timeout
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchLastSensorData(); // Fetch data again after 3 seconds
            }
        }, 3000); // 3 seconds delay
    }

    private void updateButton(JSONObject response, String sensorName, Button button, int yellowThreshold, int greenThreshold) throws JSONException {
        int sensorValue = response.getInt(sensorName);
        button.setText(sensorName + ": " + sensorValue);

        // Set button color based on sensor value
        if (sensorValue < yellowThreshold) {
            button.setBackgroundColor(Color.RED);
        } else if (sensorValue >= yellowThreshold && sensorValue < greenThreshold) {
            button.setBackgroundColor(Color.YELLOW);
        } else {
            button.setBackgroundColor(Color.GREEN);
        }

        // Set button padding
        int padding = 20; // Adjust as needed
        button.setPadding(padding, padding, padding, padding);
    }
}
