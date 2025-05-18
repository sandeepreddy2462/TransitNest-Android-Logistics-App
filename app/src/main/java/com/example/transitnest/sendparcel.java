package com.example.transitnest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class sendparcel extends AppCompatActivity {

    private AutoCompleteTextView etFrom, etTo;
    private EditText etContent, etItem, etWeightKg, etWeightGm, etInstructions, etReceiverMobile, etReceiverEmail, etReceiverName;
    private Button btnSubmit, btnCancel;
    private RequestQueue requestQueue;
    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search?format=json&q=";
    private static final String SERVER_URL = "http://10.0.2.2/phpfiles/store_parcel.php"; // Change this to your actual server URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendparcel);

        // Initialize fields
        etFrom = findViewById(R.id.sendparcel_et3);
        etTo = findViewById(R.id.sendparcel_et4);
        etContent = findViewById(R.id.sendparcel_et1);
        etItem = findViewById(R.id.sendparcel_et2);
        etWeightKg = findViewById(R.id.sendparcel_et5);
        etWeightGm = findViewById(R.id.sendparcel_et6);
        etInstructions = findViewById(R.id.sendparcel_et7);
        etReceiverMobile = findViewById(R.id.sendparcel_et8);
        etReceiverEmail = findViewById(R.id.sendparcel_et9);
        etReceiverName = findViewById(R.id.sendparcel_et10);
        btnSubmit = findViewById(R.id.sendparcel_btn3);
        btnCancel = findViewById(R.id.sendparcel_btn2);

        requestQueue = Volley.newRequestQueue(this);

        // Add AutoComplete Suggestions
        addAutoCompleteFeature(etFrom);
        addAutoCompleteFeature(etTo);

        // Submit button click event
        btnSubmit.setOnClickListener(v -> submitParcelDetails());

        // Cancel button click event
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(sendparcel.this, homepage.class);
            startActivity(intent);
            finish();
        });
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
                error -> error.printStackTrace());

        requestQueue.add(jsonArrayRequest);
    }

    private void submitParcelDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String senderEmail = sharedPreferences.getString("sessionemail", ""); // Retrieve stored email

        String content = etContent.getText().toString().trim();
        String item = etItem.getText().toString().trim();
        String senderAddress = etFrom.getText().toString().trim();
        String receiverAddress = etTo.getText().toString().trim();
        String weightKg = etWeightKg.getText().toString().trim();
        String weightGm = etWeightGm.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();
        String receiverMobile = etReceiverMobile.getText().toString().trim();
        String receiverEmail = etReceiverEmail.getText().toString().trim();
        String receiverName = etReceiverName.getText().toString().trim();

        if (content.isEmpty() || item.isEmpty() || senderAddress.isEmpty() || receiverAddress.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            Toast.makeText(this, "Parcel Details Submitted!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(sendparcel.this, myParcel.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to submit trip details.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error processing response "+e, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show())
        {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_email", senderEmail);
                params.put("content", content);
                params.put("item", item);
                params.put("sender_address", senderAddress);
                params.put("receiver_address", receiverAddress);
                params.put("weight_kgs", weightKg);
                params.put("weight_gms", weightGm);
                params.put("instruction_to_bringers", instructions);
                params.put("receiver_mobile", receiverMobile);
                params.put("receiver_email", receiverEmail);
                params.put("receiver_name", receiverName);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}

