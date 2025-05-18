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

public class discoverSender extends AppCompatActivity {

    private AutoCompleteTextView etFrom, etTo;
    private Button btnApply, btnCancel;
    private LinearLayout SendersListLayout;
    private RequestQueue requestQueue;

    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search?format=json&q=";
    private static final String SERVER_URL = "http://10.0.2.2/phpfiles/discover_senders.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_sender);

        etFrom = findViewById(R.id.sender_address);
        etTo = findViewById(R.id.receiver_address);
        btnCancel = findViewById(R.id.cancel_button);
        btnApply = findViewById(R.id.apply_button);
        SendersListLayout = findViewById(R.id.senders_list_container);

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
                        SendersListLayout.removeAllViews(); // Clear previous views
                        SendersListLayout.setVisibility(View.VISIBLE); // Make it visible

                        if (jsonArray.length() == 0) {
                            Toast.makeText(this, "No Senders found.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject traveler = jsonArray.getJSONObject(i);

                            // Extract traveler details from JSON
                            String username = traveler.getString("username");
                            String senderemail = traveler.getString("useremail");
                            String userphonenumber = traveler.getString("userphonenumber");
                            String content = traveler.getString("content");
                            String weight_kgs = traveler.getString("weight_kgs");
                            String receiver_mobile = traveler.getString("receiver_mobile");
                            String receiver_email = traveler.getString("receiver_email");
                            String receiver_name = traveler.getString("receiver_name");

                            // Format the text to display
                            String travelerInfo = "Name:  " + username + "\n"
                                    +"Sender Email:  " + senderemail + "\n"
                                    + "Number:  " + userphonenumber + "\n"
                                    + "Content:  " + content + "\n"
                                    + "Weight (kgs):  " + weight_kgs + "\n"
                                    + "Price for delivery:  " + (Integer.parseInt(weight_kgs) * 55) + "/-" + "\n"
                                    + "Receiver Mobile:  " + receiver_mobile + "\n"
                                    + "Receiver Email:  " + receiver_email + "\n"
                                    + "Receiver Name:  " + receiver_name + "\n";

                            // Create a new TextView dynamically
                            TextView travelerTextView = new TextView(this);
                            travelerTextView.setText(travelerInfo);
                            travelerTextView.setPadding(0,0,0,10);
                            travelerTextView.setTextSize(18);

                            // Connect Button
                            Button connectButton = new Button(this);
                            connectButton.setText("Connect");
                            connectButton.setPadding(20, 20, 20, 20);
                            connectButton.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                            connectButton.setTextColor(getResources().getColor(android.R.color.white));


                            // Handle "Connect" button click
                                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                                String sessionEmail = sharedPreferences.getString("sessionemail", ""); // Retrieve session email

                                if (sessionEmail.isEmpty()) {
                                    Toast.makeText(this, "Session email not found. Please log in again.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Step 1: Check if connection already exists
                                StringRequest checkRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/phpfiles/check_connect_status.php",
                                        checkResponse -> { // Renamed response variable
                                            if (checkResponse.trim().equals("Active")) {
                                                // Connection already exists, disable button
                                                connectButton.setText("Connected Already");
                                                connectButton.setEnabled(false);
                                                connectButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                                            }else if (checkResponse.trim().equals("Completed")){
                                                connectButton.setText("Delivered");
                                                connectButton.setEnabled(false);
                                                connectButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                                            } else if (checkResponse.trim().equals("no_trips")) {
// --------------------------------------------CONNECT BUTTON -----------------------------------------------------------------

                                                connectButton.setOnClickListener(v -> {
                                                    new AlertDialog.Builder(this)
                                                            .setTitle("Confirm Connection")
                                                            .setMessage("Do you want to connect with " + username + "?")
                                                            .setPositiveButton("Connect", (dialog, which) -> {
                                                    // Step 2: If no connection exists, insert a new connection
                                                StringRequest insertRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/phpfiles/connect_sender.php",
                                                        insertResponse -> { // Renamed response variable
                                                            if (insertResponse.trim().equals("success")) {
                                                                connectButton.setText("Connected");
                                                                connectButton.setEnabled(false);
                                                                connectButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));

                                                                // Navigate to "My Trips" after successful connection
                                                                Intent intent = new Intent(this, myTrips.class);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(this, "Failed to connect. Try again.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        },
                                                        error -> Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()) {
                                                    @Override
                                                    protected Map<String, String> getParams() {
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put("sessionemail", sessionEmail);
                                                        params.put("senderemail", senderemail);
                                                        params.put("from_location", etFrom.getText().toString().trim());
                                                        params.put("to_location", etTo.getText().toString().trim());
                                                        return params;
                                                    }
                                                };
                                                RequestQueue requestQueue = Volley.newRequestQueue(this);
                                                requestQueue.add(insertRequest);
                                                            })
                                                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                                            .show();
                                            });

// --------------------------------------------CONNECT BUTTON COMPLETE -----------------------------------------------------------------

                                            }
                                        },
                                        error -> Toast.makeText(this, "Failed to check connection status", Toast.LENGTH_SHORT).show()) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("sessionemail", sessionEmail);
                                        params.put("senderemail", senderemail);
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(this);
                                requestQueue.add(checkRequest);


                            // Create a Contact button dynamically
                            Button contact = new Button(this);
                            contact.setText("Call the sender");
                            contact.setPadding(20, 20, 20, 20);
                            contact.setBackgroundColor(getResources().getColor(R.color.green));
                            contact.setTextColor(getResources().getColor(android.R.color.white));
                            contact.setElevation(0); // Removes shadow

                            // Handle button click
                            contact.setOnClickListener(v -> {
                                // Open dialer with the user's phone number
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(android.net.Uri.parse("tel:" + userphonenumber));
                                startActivity(intent);
                            });

                            // Create a container for text and button
                            LinearLayout container = new LinearLayout(this);
                            container.setOrientation(LinearLayout.VERTICAL);
                            container.setPadding(20, 20, 20, 50);
                            container.setGravity(Gravity.CENTER); // Align button to the left
                            container.addView(travelerTextView);
                            container.addView(contact);
                            container.addView(connectButton);
                            contact.setGravity(Gravity.CENTER);
                            View divider = new View(this);
                            divider.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    5
                            ));
                            divider.setBackgroundColor(getResources().getColor(R.color.dark_gray));

                            SendersListLayout.addView(container);
                            SendersListLayout.addView(divider);

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

    private void navigateToHomePage() {
        Intent intent = new Intent(this, homepage.class); // Change to your homepage activity
        startActivity(intent);
        finish();
    }
}
