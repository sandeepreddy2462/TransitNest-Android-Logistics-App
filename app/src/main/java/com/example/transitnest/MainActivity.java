package com.example.transitnest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            // Check session before deciding which screen to show
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String storedEmail = sharedPreferences.getString("sessionemail", "");

            Intent intent;
            if (!storedEmail.isEmpty()) {
                intent = new Intent(MainActivity.this, homepage.class);
            } else {
                intent = new Intent(MainActivity.this, login_activity.class);
            }
            startActivity(intent);
            finish();
        }, 3000);
    }
}














