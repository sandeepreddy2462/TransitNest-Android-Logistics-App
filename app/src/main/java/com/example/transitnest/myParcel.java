package com.example.transitnest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myParcel extends AppCompatActivity {

    private LinearLayout createdParcelLayout, connectedParcelLayout;
    private Button btncreatedparcels, btnconnectedparcels, homeButton, myTripsButotn;
    private ListView createdParcelList, connectedParcelList;
    private int selectedColor = Color.parseColor("#11516b"); // Blue for selected button
    private int defaultColor = Color.parseColor("#bdc4c7");  // Gray for unselected button
    private static final String FETCH_CREATED_URL = "http://10.0.2.2/phpfiles/fetch_created_parcels.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parcel);

        // Initialize UI elements
        createdParcelLayout = findViewById(R.id.createdParcelLayout);
        connectedParcelLayout = findViewById(R.id.connectedParcelLayout);
        btncreatedparcels = findViewById(R.id.btn_created_parcels);
        btnconnectedparcels = findViewById(R.id.btn_connected_parcels);
        createdParcelList = findViewById(R.id.createdParcelList);
        connectedParcelList = findViewById(R.id.connectedParcelList);
        homeButton = findViewById(R.id.home_button);
        myTripsButotn = findViewById(R.id.trips_button);

        // Set default view
        btncreatedparcels.setBackgroundColor(selectedColor);
        btnconnectedparcels.setBackgroundColor(defaultColor);
        createdParcelLayout.setVisibility(View.VISIBLE);
        connectedParcelLayout.setVisibility(View.GONE);

        // Load trips from server
        loadCreatedParcel();
        loadConnectedParcel();

        // Toggle buttons for switching views
        btncreatedparcels.setOnClickListener(v -> {
            createdParcelLayout.setVisibility(View.VISIBLE);
            connectedParcelLayout.setVisibility(View.GONE);
            btncreatedparcels.setBackgroundColor(selectedColor);
            btnconnectedparcels.setBackgroundColor(defaultColor);
        });

        btnconnectedparcels.setOnClickListener(v -> {
            createdParcelLayout.setVisibility(View.GONE);
            connectedParcelLayout.setVisibility(View.VISIBLE);
            btnconnectedparcels.setBackgroundColor(selectedColor);
            btncreatedparcels.setBackgroundColor(defaultColor);
        });

        // Navigation buttons
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(myParcel.this, homepage.class);
            startActivity(intent);
        });

        myTripsButotn.setOnClickListener(v -> {
            Intent intent = new Intent(myParcel.this, myTrips.class);
            startActivity(intent);
        });
    }

    // Fetch Created Trips from MySQL

    private void loadCreatedParcel() {


        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String sessionEmail = sharedPreferences.getString("sessionemail", "");

        if (sessionEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_CREATED_URL,
                response -> {
                    try {
                        JSONArray tripsArray = new JSONArray(response);
                        createdParcelLayout.removeAllViews(); // Clear previous data

                        if (tripsArray.length() == 0) {
                            TextView noTripsMessage = new TextView(this);
                            noTripsMessage.setText("No Parcels found.");
                            noTripsMessage.setTextColor(Color.RED);
                            noTripsMessage.setTextSize(16);
                            createdParcelLayout.addView(noTripsMessage);
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
                            tripContainer.setPadding(30, 20, 30, 20);

                            // Trip details
                            TextView tripDetails = new TextView(this);
                            tripDetails.setText("Content: " + trip.getString("content") +
                                    " Item: " + trip.getString("item") +
                                    "\nWeight: " + trip.getString("weight_kgs") +
                                    "\nSender Address: " + trip.getString("sender_address") +
                                    "\n\nReciever Address: " + trip.getString("receiver_address") +
                                    "\nReciever Mobile: " + trip.getString("receiver_mobile")+
                                    "\nReciever Name: " + trip.getString("receiver_name"));
                            tripDetails.setTextSize(16);
                            tripDetails.setTextColor(Color.BLACK);
                            View divider = new View(this);
                            divider.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    5
                            ));
                            divider.setBackgroundColor(getResources().getColor(R.color.dark_gray));


                            tripContainer.addView(tripDetails);
                            createdParcelLayout.addView(tripContainer);
                            createdParcelLayout.addView(divider);

                        }

                    } catch (JSONException e) {
                        Toast.makeText(myParcel.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    // Load Connected Trips (Dummy Data)
    private void loadConnectedParcel() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String sessionEmail = sharedPreferences.getString("sessionemail", "");

        if (sessionEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String FETCH_CONNECTED_URL = "http://10.0.2.2/phpfiles/fetch_connected_parcels.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_CONNECTED_URL,
                response -> {
                    try {
                        JSONArray parcelsArray = new JSONArray(response);
                        connectedParcelLayout.removeAllViews(); // Clear previous data

                        if (parcelsArray.length() == 0) {
                            TextView noParcelsMessage = new TextView(this);
                            noParcelsMessage.setText("No Connected Parcels found.");
                            noParcelsMessage.setTextColor(Color.RED);
                            noParcelsMessage.setTextSize(16);
                            connectedParcelLayout.addView(noParcelsMessage);
                            return;
                        }

                        for (int i = 0; i < parcelsArray.length(); i++) {
                            JSONObject parcel = parcelsArray.getJSONObject(i);

                            // Create container for each parcel
                            LinearLayout parcelContainer = new LinearLayout(this);
                            parcelContainer.setOrientation(LinearLayout.VERTICAL);
                            parcelContainer.setPadding(20, 20, 20, 20);
                            parcelContainer.setBackgroundColor(Color.parseColor("#E3F2FD"));
                            parcelContainer.setElevation(5);
                            parcelContainer.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            parcelContainer.setPadding(30, 20, 30, 20);

                            // Parcel details
                            TextView parcelDetails = new TextView(this);
                            parcelDetails.setText(
                                    "Username: " + parcel.getString("username") +
                                            "\nPhone: " + parcel.getString("userphonenumber") +
                                            "\nTraveller Email: " + parcel.getString("travelleremail") +
                                            "\nFrom: " + parcel.getString("from_location") +
                                            "\nTo: " + parcel.getString("to_location"));
                            parcelDetails.setTextSize(16);
                            parcelDetails.setTextColor(Color.BLACK);

                            // Status Button
                            Button statusButton = new Button(this);
                            String status = parcel.getString("status_of_trip");

                            statusButton.setText(status);
                            statusButton.setTextSize(14);
                            statusButton.setPadding(10, 10, 10, 10);
                            statusButton.setEnabled(false);

                            if ("Active".equals(status)) {
                                statusButton.setBackgroundColor(getResources().getColor(R.color.green));
                                statusButton.setTextColor(Color.WHITE);
                            } else if ("Completed".equals(status)) {
                                statusButton.setBackgroundColor(Color.GRAY);
                                statusButton.setTextColor(Color.WHITE);
                            }

                            // Divider
                            View divider = new View(this);
                            divider.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    5
                            ));
                            divider.setBackgroundColor(getResources().getColor(R.color.dark_gray));

                            // Add components to container
                            parcelContainer.addView(parcelDetails);
                            parcelContainer.addView(statusButton);
                            connectedParcelLayout.addView(parcelContainer);
                            connectedParcelLayout.addView(divider);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(myParcel.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

}
