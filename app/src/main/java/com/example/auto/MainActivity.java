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

    private Button button;
    private Handler handler = new Handler();
    private RequestQueue requestQueue;
    private final String URL = "http://192.168.43.60/get_sensor_data.php"; // Updated URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        requestQueue = Volley.newRequestQueue(this);

        fetchLastSensorData();
    }

    private void fetchLastSensorData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String sensorName = response.getString("sensor_name");
                            int sensorValue = response.getInt("sensor_value");
                            button.setText(sensorName + ": " + sensorValue);

                            // Set button color based on sensor value
                            if (sensorValue < 15) {
                                button.setBackgroundColor(Color.RED);
                            } else if (sensorValue >= 15 && sensorValue < 40) {
                                button.setBackgroundColor(Color.YELLOW);
                            } else {
                                button.setBackgroundColor(Color.GREEN);
                            }

                            // Set button padding
                            int padding = 20; // Adjust as needed
                            button.setPadding(padding, padding, padding, padding);

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
}
