package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class review extends AppCompatActivity {

    private EditText etComment, etRate, etUserEmail;
    private Button btnSubmit, btnGoBack;
    private ConnectHelper connectHelper;
    private String userEmail; // To store the user's email passed via Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize views
        etComment = findViewById(R.id.et_comment);
        etRate = findViewById(R.id.et_rate);
        etUserEmail = findViewById(R.id.et_user_email); // Hidden field

        btnSubmit = findViewById(R.id.btn_submit);
        btnGoBack = findViewById(R.id.btn_go_back);

        // Initialize database connection helper
        connectHelper = new ConnectHelper(this);
        Intent intent = getIntent();

        // Get the email from Intent
        if (intent != null && intent.hasExtra("email")) {
            userEmail = intent.getStringExtra("email");
            etUserEmail.setText(userEmail); // Store it in the hidden field if needed
        } else {
            Toast.makeText(this, "User email not provided!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no email is provided
            return; // Added return to prevent further execution
        }

        // Handle Submit button click
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        // Handle Go Back button click
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close current activity and go back
            }
        });
    }

    private void submitFeedback() {
        String comment = etComment.getText().toString().trim();
        String rateStr = etRate.getText().toString().trim();

        if (comment.isEmpty() || rateStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int rate;
        try {
            rate = Integer.parseInt(rateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rating. Please enter a number between 1 and 5.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rate < 1 || rate > 5) {
            Toast.makeText(this, "Rate must be between 1 and 5.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current system time
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Construct SQL insert query
        String insertQuery = "INSERT INTO FeedBacks (Comment, dateTime, Rate, UserEmail) VALUES (?, ?, ?, ?)";

        boolean isInserted = false;
        try (Connection connection = connectHelper.getConnection()) {
            if (connection != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, comment);
                    preparedStatement.setTimestamp(2, currentTimestamp); // Use current system time
                    preparedStatement.setInt(3, rate);
                    preparedStatement.setString(4, userEmail); // Use email from intent
                    isInserted = preparedStatement.executeUpdate() > 0;
                }
            } else {
                Toast.makeText(this, "Connection failed! Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error submitting feedback: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (isInserted) {
            Intent i = new Intent(review.this,Successfully.class);
            startActivity(i);
            Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Failed to submit feedback.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etComment.setText("");
        etRate.setText("");
        etUserEmail.setText(""); // Optionally clear the email field
    }
}
