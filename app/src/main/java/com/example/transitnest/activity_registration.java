package com.example.transitnest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import java.util.HashMap;
import java.util.Map;

public class activity_registration extends AppCompatActivity {
    EditText username, useremail, userphonenumber, userpassword;
    Button register, loginbutton;
    private static final String SERVER_URL = "http://10.0.2.2/phpfiles/register.php"; // Change this to your actual server URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = findViewById(R.id.Name);
        useremail = findViewById(R.id.email_signup);
        userphonenumber = findViewById(R.id.phonenumber_signup);
        userpassword = findViewById(R.id.signup_password_input);
        register = findViewById(R.id.signup);
        loginbutton = findViewById(R.id.login_button);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_registration.this, login_activity.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Registering", "Please wait...", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(activity_registration.this, response, Toast.LENGTH_LONG).show();

                        if (response.contains("success")) {
                            Intent intent = new Intent(activity_registration.this, login_activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(activity_registration.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString().trim());
                params.put("useremail", useremail.getText().toString().trim());
                params.put("userphonenumber", userphonenumber.getText().toString().trim());
                params.put("userpassword", userpassword.getText().toString().trim());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}