package com.example.transitnest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

public class profilepage extends AppCompatActivity {
    TextView nameTextView, phoneTextView, emailTextView;
    Button signOut, backButton, changepassword;
    private static final String PROFILE_URL = "http://10.0.2.2/phpfiles/profile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);
        Toast.makeText(this, "Profile Page Loaded", Toast.LENGTH_LONG).show();

        nameTextView = findViewById(R.id.name_as_per_card);
        phoneTextView = findViewById(R.id.phonenumber_in_profile);
        emailTextView = findViewById(R.id.email_in_profile);
        signOut = findViewById(R.id.logout);
        backButton = findViewById(R.id.previous_icon);
        changepassword = findViewById(R.id.change_password_button);

        fetchUserProfile();

        signOut.setOnClickListener(v -> redirectToLogin());
        backButton.setOnClickListener(v -> redirectToHome());
        changepassword.setOnClickListener(v -> redirectToChangePassword());
    }

    private void fetchUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String sessionEmail = sharedPreferences.getString("sessionemail", null);

        if (sessionEmail == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PROFILE_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            String username = jsonResponse.getString("username");
                            String userPhone = jsonResponse.getString("userphonenumber");
                            String userEmail = jsonResponse.getString("useremail");

                            nameTextView.setText(username);
                            phoneTextView.setText(userPhone);
                            emailTextView.setText(userEmail);
                        } else {
                            Toast.makeText(profilepage.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(profilepage.this, "JSON Parse Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(profilepage.this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sessionemail", sessionEmail);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void redirectToHome(){
        startActivity(new Intent(profilepage.this, homepage.class));
        finish();
    }
    private void redirectToLogin() {
        // Clear session data
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("sessionemail"); // Remove only session email
        editor.apply(); // Apply changes

        // Redirect to login activity
        Intent intent = new Intent(profilepage.this, login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent going back
        startActivity(intent);
        finish();
    }
    private void redirectToChangePassword(){
        startActivity(new Intent(profilepage.this, change_password.class));
        finish();
    }

}
