package com.example.transitnest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class myTrips extends AppCompatActivity {

    private LinearLayout createdTripsLayout, connectedTripsLayout;
    private Button btnCreatedTrips, btnConnectedTrips, homeButton, myParcelButton;
    private int selectedColor = Color.parseColor("#11516b"); // Blue for selected button
    private int defaultColor = Color.parseColor("#bdc4c7");  // Gray for unselected button
    private static final String FETCH_CREATED_TRIPS_URL = "http://10.0.2.2/phpfiles/fetch_created_trips.php";
    private static final String FETCH_CONNECTED_TRIPS_URL = "http://10.0.2.2/phpfiles/fetch_connected_trips.php"; // Update with actual URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        // Initialize UI elements
        createdTripsLayout = findViewById(R.id.createdTripsLayout);
        connectedTripsLayout = findViewById(R.id.connectedTripsLayout);
        btnCreatedTrips = findViewById(R.id.btn_created_trips);
        btnConnectedTrips = findViewById(R.id.btn_connected_trips);
        homeButton = findViewById(R.id.home_button);
        myParcelButton = findViewById(R.id.myparcel_button);

        // Set default view
        btnCreatedTrips.setBackgroundColor(selectedColor);
        btnConnectedTrips.setBackgroundColor(defaultColor);
        createdTripsLayout.setVisibility(View.VISIBLE);
        connectedTripsLayout.setVisibility(View.GONE);

        // Load trips from server
        loadCreatedTrips();
        loadConnectedTrips();

        // Toggle buttons for switching views
        btnCreatedTrips.setOnClickListener(v -> {
            createdTripsLayout.setVisibility(View.VISIBLE);
            connectedTripsLayout.setVisibility(View.GONE);
            btnCreatedTrips.setBackgroundColor(selectedColor);
            btnConnectedTrips.setBackgroundColor(defaultColor);
        });

        btnConnectedTrips.setOnClickListener(v -> {
            createdTripsLayout.setVisibility(View.GONE);
            connectedTripsLayout.setVisibility(View.VISIBLE);
            btnConnectedTrips.setBackgroundColor(selectedColor);
            btnCreatedTrips.setBackgroundColor(defaultColor);
        });

        // Navigation buttons
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(myTrips.this, homepage.class);
            startActivity(intent);
        });

        myParcelButton.setOnClickListener(v -> {
            Intent intent = new Intent(myTrips.this, myParcel.class);
            startActivity(intent);
        });
    }

    // Fetch Created Trips from MySQL
    private void loadCreatedTrips() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String sessionEmail = sharedPreferences.getString("sessionemail", "");

        if (sessionEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_CREATED_TRIPS_URL,
                response -> {
                    try {
                        JSONArray tripsArray = new JSONArray(response);
                        createdTripsLayout.removeAllViews(); // Clear previous data
                        if (tripsArray.length() == 0) {
                            TextView noTripsMessage = new TextView(this);
                            noTripsMessage.setText("No trips found.");
                            noTripsMessage.setTextColor(Color.RED);
                            noTripsMessage.setTextSize(16);
                            createdTripsLayout.addView(noTripsMessage);
                            return;
                        }

                        for (int i = 0; i < tripsArray.length(); i++) {
                            JSONObject trip = tripsArray.getJSONObject(i);

                            // Create a new container for each trip
                            LinearLayout tripContainer = new LinearLayout(this);
                            tripContainer.setOrientation(LinearLayout.VERTICAL);
                            tripContainer.setPadding(20, 20, 20, 20);
                            tripContainer.setBackgroundColor(Color.parseColor("#E3F2FD"));
                            tripContainer.setElevation(5);
                            tripContainer.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            // Trip details
                            TextView tripDetails = new TextView(this);
                            tripDetails.setText("From: " + trip.getString("travelling_from") +
                                    " -> To: " + trip.getString("travelling_to") +
                                    "\n\nDeparture: " + trip.getString("departure_datetime") +
                                    "\nArrival: " + trip.getString("arrival_datetime") +
                                    "\nMode: " + trip.getString("transportation_mode"));
                            tripDetails.setTextSize(16);
                            tripDetails.setTextColor(Color.BLACK);

                            // Add views to trip container
                            tripContainer.addView(tripDetails);
                            View divider = new View(this);
                            divider.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    5
                            ));
                            divider.setBackgroundColor(getResources().getColor(R.color.dark_gray));

                            createdTripsLayout.addView(tripContainer);
                            createdTripsLayout.addView(divider);

                        }

                    } catch (JSONException e) {
                        Toast.makeText(myTrips.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("adminemail", sessionEmail);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    // Fetch Connected Trips from MySQL
    private void loadConnectedTrips() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String sessionEmail = sharedPreferences.getString("sessionemail", "");

        if (sessionEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_CONNECTED_TRIPS_URL,
                response -> {
                    try {
                        JSONArray tripsArray = new JSONArray(response);
                        connectedTripsLayout.removeAllViews(); // Clear previous data
                        if (tripsArray.length() == 0) {
                            TextView noTripsMessage = new TextView(this);
                            noTripsMessage.setText("No connected trips found.");
                            noTripsMessage.setTextColor(Color.RED);
                            noTripsMessage.setTextSize(16);
                            connectedTripsLayout.addView(noTripsMessage);
                            return;
                        }

                        for (int i = 0; i < tripsArray.length(); i++) {
                            JSONObject trip = tripsArray.getJSONObject(i);
                            String status = trip.getString("status_of_trip");

                            // Create a new container for each connected trip
                            LinearLayout tripContainer = new LinearLayout(this);
                            tripContainer.setOrientation(LinearLayout.VERTICAL);
                            tripContainer.setPadding(20, 20, 20, 20);
                            tripContainer.setBackgroundColor(Color.parseColor("#E8F5E9"));
                            tripContainer.setElevation(5);
                            tripContainer.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            // Trip details
                            TextView tripDetails = new TextView(this);
                            tripDetails.setText("\nContent: " + trip.getString("content") +
                                    "\nWeight: " + trip.getString("weight_kgs") + " kgs" +
                                    "\n\nSender: " + trip.getString("username") +
                                    "\nSender Email: " + trip.getString("senderemail") +
                                    "\nSender Phone: " + trip.getString("userphonenumber") +
                                    "\n\nFrom: " + trip.getString("from_location") +
                                    "\n-> To: " + trip.getString("to_location") +
                                    "\n\nReceiver: " + trip.getString("receiver_name") +
                                    "\nReceiver Email: " + trip.getString("receiver_email") +
                                    "\nReceiver Mobile: " + trip.getString("receiver_mobile"));
                            tripDetails.setTextSize(16);
                            tripDetails.setTextColor(Color.BLACK);

                            // First button: Status of the trip
                            Button statusButton = new Button(this);
                            statusButton.setText(status);
                            statusButton.setPadding(10, 10, 10, 10);
                            statusButton.setTextSize(14);
                            statusButton.setTextColor(Color.WHITE);
                            if (status.equals("Active")) {
                                statusButton.setBackgroundColor(getResources().getColor(R.color.green));
                            } else if (status.equals("Completed")) {
                                statusButton.setBackgroundColor(Color.GRAY);
                                statusButton.setEnabled(false);
                            }

                            // Second button: Delivered
                            Button deliveredButton = new Button(this);
                            deliveredButton.setText("Delivered");
                            deliveredButton.setPadding(10, 10, 10, 10);
                            deliveredButton.setTextSize(14);
                            deliveredButton.setTextColor(Color.WHITE);
                            deliveredButton.setBackgroundColor(getResources().getColor(R.color.blue_button));

                            // Disable the "Delivered" button if the trip is already completed
                            if (status.equals("Completed")) {
                                deliveredButton.setEnabled(false);
                                deliveredButton.setBackgroundColor(Color.GRAY);
                                statusButton.setVisibility(View.GONE);
                                deliveredButton.setText("Delivered / Completed");
                            }

                            deliveredButton.setOnClickListener(v -> {

                                new AlertDialog.Builder(this)
                                        .setTitle("Confirm Delivery")
                                        .setMessage("Did you delivered the packet!")
                                        .setPositiveButton("Delivered", (dialog, which) -> {
                                try {
                                    updateTripStatus(
                                            trip.getString("senderemail"),
                                            trip.getString("from_location"),
                                            trip.getString("to_location"),
                                            sessionEmail,
                                            statusButton,
                                            deliveredButton
                                    );
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                    .show();
                            });

                            // Add views to trip container
                            tripContainer.addView(tripDetails);
                            tripContainer.addView(statusButton);
                            tripContainer.addView(deliveredButton);

                            View divider = new View(this);
                            divider.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 5));
                            divider.setBackgroundColor(getResources().getColor(R.color.dark_gray));

                            connectedTripsLayout.addView(tripContainer);
                            connectedTripsLayout.addView(divider);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(myTrips.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("adminemail", sessionEmail);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    // Function to update trip status
    private void updateTripStatus(String senderEmail, String fromLocation, String toLocation, String sessionEmail, Button statusButton, Button deliveredButton) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String UPDATE_STATUS_URL = "http://10.0.2.2/phpfiles/update_status.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_STATUS_URL,
                response -> {
                    if (response.trim().equals("success")) {
                        statusButton.setText("Completed");
                        statusButton.setBackgroundColor(Color.GRAY);
                        statusButton.setEnabled(false);
                        statusButton.setVisibility(View.GONE);

                        deliveredButton.setText("Delivered");
                        deliveredButton.setBackgroundColor(Color.GRAY);
                        deliveredButton.setEnabled(false);

                        Toast.makeText(this, "Trip status updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("senders_email", senderEmail);
                params.put("from_location", fromLocation);
                params.put("to_location", toLocation);
                params.put("adminemail", sessionEmail);
                return params;
            }
        };

        queue.add(stringRequest);
    }


}
