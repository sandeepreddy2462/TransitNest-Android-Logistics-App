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

public class login_activity extends AppCompatActivity {
    EditText useremail, userpassword;
    Button login, createAccount;
    ProgressDialog progressDialog;
    private static final String LOGIN_URL = "http://10.0.2.2/phpfiles/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        useremail = findViewById(R.id.signin_input);
        userpassword = findViewById(R.id.signin_password);
        login = findViewById(R.id.login);
        createAccount = findViewById(R.id.create_account);


        login.setOnClickListener(view -> loginUser());
        createAccount.setOnClickListener(view -> CreateAccount());

    }


    private void loginUser() {
        String email = useremail.getText().toString().trim();
        String password = userpassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(this, "Logging in", "Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        // Debugging: Show server response
                        Toast.makeText(login_activity.this, "Response: " + response, Toast.LENGTH_SHORT).show();

                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            // Store session data
                            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("sessionemail", email);
                            editor.apply();

                            Toast.makeText(login_activity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login_activity.this, homepage.class));
                            finish();
                        } else {
                            Toast.makeText(login_activity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(login_activity.this, "JSON Parse Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(login_activity.this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("useremail", email);
                params.put("checkpassword", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void CreateAccount(){
        startActivity(new Intent(login_activity.this, activity_registration.class));
    }
}
