package com.example.transitnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Profile Button
        Button profileButton = findViewById(R.id.homepage_profile);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepage.this, profilepage.class);
            startActivity(intent);
        });
        // Notification Button
//        Button notificationButton = findViewById(R.id.homepage_notification);
//        notificationButton.setOnClickListener(v -> navigateTo(NotificationActivity.class));

        // Send Parcel Button
        Button sendParcelButton = findViewById(R.id.homepage_button1);
        sendParcelButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepage.this, sendparcel.class);
            startActivity(intent);
        });
        // Carry Parcel Button
        Button carryParcelButton = findViewById(R.id.homepage_button2);
        carryParcelButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepage.this, carryparcel.class);
            startActivity(intent);
        });
        // Discover Sender Button
        Button discoverSenderButton = findViewById(R.id.homepage_button3);
        discoverSenderButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepage.this, discoverSender.class);
            startActivity(intent);
        });
        // Discover Bringer Button
        Button discoverBringerButton = findViewById(R.id.homepage_button4);
        discoverBringerButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepage.this, discoverBringer.class);
            startActivity(intent);
        });

        // My Trips Button
        Button tripsButton = findViewById(R.id.trips_button);
        tripsButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepage.this, myTrips.class);
            startActivity(intent);
        });
        // My Parcel Button
        Button myParcelButton = findViewById(R.id.myparcel_button);
        myParcelButton.setOnClickListener(v -> {
            Intent intent = new Intent(homepage.this, myParcel.class);
            startActivity(intent);
        });
    }

}
