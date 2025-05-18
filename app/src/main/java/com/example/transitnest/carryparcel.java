package com.example.transitnest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class carryparcel extends AppCompatActivity {
    private AutoCompleteTextView etFrom, etTo;
    private EditText etDeparture, etArrival;
    private RadioGroup transportGroup;
    private Button btnSubmit, btnCancel;
    private RequestQueue requestQueue;
    private String selectedTransportMode;
    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search?format=json&q=";
    private static final String SERVER_URL = "http://10.0.2.2/phpfiles/carry_parcel.php"; // Change this to your actual server URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carryparcel);

        etFrom = findViewById(R.id.carryparcel_actv1);
        etTo = findViewById(R.id.carryparcel_actv2);
        etDeparture = findViewById(R.id.carryparcel_et3);
        etArrival = findViewById(R.id.carryparcel_et4);
        transportGroup = findViewById(R.id.carryparcel_rg1);
        btnSubmit = findViewById(R.id.sendparcel_btn3);
        btnCancel = findViewById(R.id.sendparcel_btn2);

        requestQueue = Volley.newRequestQueue(this);
        addAutoCompleteFeature(etFrom);
        addAutoCompleteFeature(etTo);

        // Handle transport mode selection
        transportGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton != null) {
                selectedTransportMode = selectedRadioButton.getText().toString();
            }
        });

        etDeparture.setOnClickListener(v -> showDateTimePicker(etDeparture));
        etArrival.setOnClickListener(v -> showDateTimePicker(etArrival));

        btnSubmit.setOnClickListener(v -> submitTripDetails());
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(carryparcel.this, homepage.class);
            startActivity(intent);
            finish();
        });
    }
    private void showDateTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);

                                String formattedDateTime = String.format(Locale.getDefault(),
                                        "%04d-%02d-%02d %02d:%02d:%02d",
                                        year, month + 1, dayOfMonth, hourOfDay, minute, 0);
                                editText.setText(formattedDateTime);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
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
        if (query.length() < 3) return; // Avoid unnecessary API calls

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

    private void submitTripDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String travelleremail = sharedPreferences.getString("sessionemail", ""); // Retrieve stored email

        String fromLocation = etFrom.getText().toString().trim();
        String toLocation = etTo.getText().toString().trim();
        String departureTime = etDeparture.getText().toString().trim();
        String arrivalTime = etArrival.getText().toString().trim();

        if (fromLocation.isEmpty() || toLocation.isEmpty() || departureTime.isEmpty() || arrivalTime.isEmpty() || selectedTransportMode == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            Toast.makeText(this, "Trip details submitted successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(carryparcel.this, myTrips.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to submit trip details.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show())
        {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("travelleremail", travelleremail);
                params.put("travelling_from", fromLocation);
                params.put("travelling_to", toLocation);
                params.put("departure_datetime", departureTime);
                params.put("arrival_datetime", arrivalTime);
                params.put("transportation_mode", selectedTransportMode);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
