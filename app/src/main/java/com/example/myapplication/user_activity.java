package com.example.myapplication; // Change this to your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class user_activity extends AppCompatActivity {

    private TextView textViewProfileName;
    private TextView textViewAccountNumber;
    private TextView textViewIDNumber;
    private TextView textViewPhoneNumber;
    private TextView textViewEmail;

    // User Information
    private String username;
    private String email;
    private String role; // Add role variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user); // Change to your layout file name

        // Initialize TextViews
        textViewProfileName = findViewById(R.id.textViewProfileName);
        textViewAccountNumber = findViewById(R.id.textViewAccountNumber);
        textViewIDNumber = findViewById(R.id.textViewIDNumber);
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        textViewEmail = findViewById(R.id.textViewEmail);

        // Initialize Buttons
        Button buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);
        Button btnTransferMoney = findViewById(R.id.btnTransferMoney);
        Button btnNotification = findViewById(R.id.btnNotifications);
        Button btnPreviousTransactions=findViewById(R.id.btnPreviousTransactions);
        Button btnBookSession = findViewById(R.id.btnBookSession);
Button btnBalance =findViewById(R.id.btnViewBalance);
        // Retrieve data from Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        role = intent.getStringExtra("role");

        // Set the retrieved data to the TextViews
        textViewProfileName.setText(username);
        textViewEmail.setText("Email: " + email); // Display email
        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewActivity(balance.class);
            }
        });
btnPreviousTransactions.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startNewActivity(UserTransaction.class);
    }
});
        // Set onClick listener for the Transfer Money button
        btnTransferMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewActivity(sendMoney.class);
            }
        });

        // Set onClick listener for the Update Profile button
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Update Profile Activity
                startNewActivity(updateprofile.class);
            }
        });

        // Set onClick listener for the Notification button
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Notification Activity
                startNewActivity(UserNotifications.class);
            }
        });

        // Set onClick listener for the Review button


        // Set onClick listener for the Book Session button
        btnBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Book Session Activity
                startNewActivity(BookSessionActivity.class); // Replace with your actual booking activity
            }
        });
    }

    // Method to start a new activity with user data
    private void startNewActivity(Class<?> activityClass) {
        Intent intent = new Intent(user_activity.this, activityClass);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("role", role); // Pass the role
        startActivity(intent);
    }
}
