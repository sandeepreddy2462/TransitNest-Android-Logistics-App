package com.example.transitnest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class change_password extends AppCompatActivity {

    private EditText newPassword, confirmPassword;
    private Button applyButton, cancel_button;
    private ProgressDialog progressDialog;
    private static final String CHANGE_PASSWORD_URL = "http://10.0.2.2/phpfiles/change_password.php"; // Update with your actual server URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Retrieve session email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String sessionEmail = sharedPreferences.getString("sessionemail", null);

        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        applyButton = findViewById(R.id.Change_button);
        cancel_button = findViewById(R.id.cancel_button);

        applyButton.setOnClickListener(view -> {
            if (sessionEmail != null) {
                validatePasswords(sessionEmail);
            } else {
                Toast.makeText(change_password.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(change_password.this, login_activity.class));
                finish();
            }
        });
        cancel_button.setOnClickListener(view -> {
            startActivity(new Intent(change_password.this, profilepage.class));
            finish();
        });
    }

    private void validatePasswords(String sessionEmail) {
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            confirmPassword.setError("Passwords do not match!");
            return;
        }

        changePassword(sessionEmail, confirmPass);
    }

    private void changePassword(String email, String confirmPass) {
        progressDialog = ProgressDialog.show(this, "Updating Password", "Please wait...", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHANGE_PASSWORD_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                    Toast.makeText(change_password.this, "Response: " + response, Toast.LENGTH_SHORT).show();
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Toast.makeText(change_password.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(change_password.this, login_activity.class));
                        finish();
                    } else {
                        Toast.makeText(change_password.this, "Failed to update password. Try again!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
            Toast.makeText(change_password.this, "JSON Parse Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(change_password.this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sessionemail", email);
                params.put("newpassword", confirmPass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
