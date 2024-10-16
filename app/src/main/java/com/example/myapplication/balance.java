package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class balance extends AppCompatActivity {

    private TextView tvAccountNumber, tvBalance;
    private Button btnGoBack;
    private ConnectHelper connectHelper;
    private String userEmail; // Email will be passed through intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        // Initialize views
        tvAccountNumber = findViewById(R.id.tv_account_number);
        tvBalance = findViewById(R.id.tv_balance);
        btnGoBack = findViewById(R.id.btn_go_back);

        // Get user email from intent
        userEmail = getIntent().getStringExtra("email");

        // Initialize database connection
        connectHelper = new ConnectHelper(this);

        // Load account details
        loadAccountDetails();

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close current activity
            }
        });
    }

    private void loadAccountDetails() {
        String query = "SELECT TOP 1 [AccountNumber], [Balance] FROM [db_aae335_bankdb].[dbo].[BankAccounts] WHERE [UserEmail] = '" + userEmail + "'";
        ResultSet resultSet = connectHelper.getData(query);
        try {
            if (resultSet != null && resultSet.next()) {
                String accountNumber = resultSet.getString("AccountNumber");
                double balance = resultSet.getDouble("Balance");
                tvAccountNumber.setText("Account Number: " + accountNumber);
                tvBalance.setText("Balance: " + balance);
            } else {
                Toast.makeText(this, "No account details found", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading account details", Toast.LENGTH_SHORT).show();
        }
    }
}
