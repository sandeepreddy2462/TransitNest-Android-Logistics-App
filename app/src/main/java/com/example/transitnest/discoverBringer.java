package com.example.transitnest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class discoverBringer extends AppCompatActivity {

    private AutoCompleteTextView etFrom, etTo;
    private Button btnApply, btnCancel;
    private LinearLayout travelerListLayout;
    private RequestQueue requestQueue;

    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search?format=json&q=";
    private static final String SERVER_URL = "http://10.0.2.2/phpfiles/discover_bringers.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_bringer);

        etFrom = findViewById(R.id.travel_from);
        etTo = findViewById(R.id.travel_to);
        btnCancel = findViewById(R.id.cancel_button);
        btnApply = findViewById(R.id.apply_button);
        travelerListLayout = findViewById(R.id.travelers_list_container);

        requestQueue = Volley.newRequestQueue(this);

        addAutoCompleteFeature(etFrom);
        addAutoCompleteFeature(etTo);

        btnApply.setOnClickListener(v -> fetchTravelerDetails());
        btnCancel.setOnClickListener(v -> navigateToHomePage());
    }

    private void addAutoCompleteFeature(AutoCompleteTextView editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fetchLocationSuggestions(charSequence.toString(), editText);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void fetchLocationSuggestions(String query, AutoCompleteTextView editText) {
        if (query.length() < 3) return;

        String url = NOMINATIM_API + query.replace(" ", "%20");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<String> suggestions = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject location = response.getJSONObject(i);
                            suggestions.add(location.getString("display_name"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
                        editText.setAdapter(adapter);
                        editText.showDropDown();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace);

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchTravelerDetails() {
        String fromLocation = etFrom.getText().toString().trim();
        String toLocation = etTo.getText().toString().trim();

        if (fromLocation.isEmpty() || toLocation.isEmpty()) {
            Toast.makeText(this, "Please select both From and To locations", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        travelerListLayout.removeAllViews(); // Clear previous views
                        travelerListLayout.setVisibility(View.VISIBLE);

                        if (jsonArray.length() == 0) {
                            Toast.makeText(this, "No travelers found.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                        String sessionEmail = sharedPreferences.getString("sessionemail", "");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject traveler = jsonArray.getJSONObject(i);

                            String username = traveler.getString("username");
                            String traveller_email = traveler.getString("useremail");
                            String userphonenumber = traveler.getString("userphonenumber");
                            String departureDateTime = traveler.getString("departure_datetime");
                            String arrivalDateTime = traveler.getString("arrival_datetime");
                            String transportMode = traveler.getString("transportation_mode");

                            // Create layout components
                            TextView travelerTextView = new TextView(this);
                            travelerTextView.setText("Name: " + username + "\n" +
                                    "Number: " + userphonenumber + "\n" +
                                    "Traveller Email: " + traveller_email + "\n" +
                                    "Departure: " + departureDateTime + "\n" +
                                    "Arrival: " + arrivalDateTime + "\n" +
                                    "Transport: " + transportMode + "\n");
                            travelerTextView.setPadding(10, 10, 10, 10);
                            travelerTextView.setTextSize(18);

                            Button connectButton = new Button(this);
                            connectButton.setText("Connect");
                            connectButton.setPadding(20, 20, 20, 20);
                            connectButton.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                            connectButton.setTextColor(getResources().getColor(android.R.color.white));





                            Button contactButton = new Button(this);
                            contactButton.setText("Call the Bringer");
                            contactButton.setPadding(20, 20, 20, 20);
                            contactButton.setBackgroundColor(getResources().getColor(R.color.green));
                            contactButton.setTextColor(getResources().getColor(android.R.color.white));
                            contactButton.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(android.net.Uri.parse("tel:" + userphonenumber));
                                startActivity(intent);
                            });

                            LinearLayout container = new LinearLayout(this);
                            container.setOrientation(LinearLayout.VERTICAL);
                            container.setPadding(20, 20, 20, 20);
                            container.setGravity(Gravity.CENTER);
                            container.addView(travelerTextView);
                            container.addView(contactButton);
                            container.addView(connectButton);

                            View divider = new View(this);
                            divider.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 5
                            ));
                            divider.setBackgroundColor(getResources().getColor(R.color.dark_gray));

                            travelerListLayout.addView(container);
                            travelerListLayout.addView(divider);

                            // Check existing connection status
                            checkParcelStatus(fromLocation, toLocation, sessionEmail, traveller_email, connectButton);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("from_location", fromLocation);
                params.put("to_location", toLocation);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    // Function to check status_of_parcel
    private void checkParcelStatus(String fromLocation, String toLocation, String sessionEmail, String travellerEmail, Button connectButton) {

        StringRequest statusRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/phpfiles/check_parcel_status.php",
                response -> {
                    if (response.equals("Active")) {
                        connectButton.setEnabled(false);
                        connectButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                        connectButton.setText("Connected");
                    } else if (response.trim().equals("Completed")){
                        connectButton.setText("Delivered");
                        connectButton.setEnabled(false);
                        connectButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    } else if (response.trim().equals("no_parcel")) {
// --------------------------------------------CONNECT BUTTON -----------------------------------------------------------------

                        connectButton.setOnClickListener(v -> {
                            new AlertDialog.Builder(this)
                                    .setTitle("Confirm Connection")
                                    .setMessage("Do you want to connect with "  + "?")
                                    .setPositiveButton("Connect", (dialog, which) -> {
                            StringRequest connectRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/phpfiles/connect_parcel.php",
                                    inresponse -> {
                                        if (inresponse.trim().equals("success")) {
                                            connectButton.setEnabled(false);
                                            connectButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                                            connectButton.setText("Connected");
                                            Toast.makeText(this, "Connection established successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    error -> error.printStackTrace()) {

                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("from_location", fromLocation);
                                    params.put("to_location", toLocation);
                                    params.put("admin_email", sessionEmail);
                                    params.put("traveller_email", travellerEmail);
                                    return params;
                                }
                            };

                            requestQueue.add(connectRequest);
                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                    .show();
                        });

// --------------------------------------------CONNECT BUTTON COMPLETE -----------------------------------------------------------------

                    }
                },
                error -> error.printStackTrace()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("from_location", fromLocation);
                params.put("to_location", toLocation);
                params.put("admin_email", sessionEmail);
                params.put("traveller_email", travellerEmail);
                return params;
            }
        };

        requestQueue.add(statusRequest);
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, homepage.class); // Change to your homepage activity
        startActivity(intent);
        finish();
    }
}
