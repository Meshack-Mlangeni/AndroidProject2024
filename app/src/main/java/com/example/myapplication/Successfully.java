package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Successfully extends AppCompatActivity {

    private TextView tvMessage;
    private Button btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfully); // Ensure this matches your layout file

        tvMessage = findViewById(R.id.tvMessage); // Retrieve the TextView
        btnGoBack = findViewById(R.id.btnGoBack); // Retrieve the Go Back button

        // Set the success message
        tvMessage.setText("Thank you! You have successfully submitted your feedback.");

        // Handle Go Back button click
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Successfully.this,user_activity.class);
             startActivity(i); // Close this activity and go back to the previous one
            }
        });
    }
}
