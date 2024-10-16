package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    ConnectHelper connectHelper;
    EditText emailInput, passwordInput;
    Button loginButton;
    CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectHelper = new ConnectHelper(this);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        rememberMeCheckBox = findViewById(R.id.remember_me);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    new LoginTask(email, password).execute();
                } else {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // AsyncTask to perform login
    private class LoginTask extends AsyncTask<Void, Void, String[]> {
        String email, password;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            // Custom login logic without using predefined login methods
            Connection connection = connectHelper.getConnection();
            if (connection != null) {
                try {
                    // SQL query to select user role and username
                    String query = "SELECT UserRole, UserName FROM AspNetUsers WHERE Email = ? AND confPassword = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, email);
                    statement.setString(2, password);
                    ResultSet resultSet = statement.executeQuery();

                    // If a valid user is returned, handle role-based navigation
                    if (resultSet.next()) {
                        String role = resultSet.getString("UserRole");  // Get UserRole
                        String username = resultSet.getString("UserName");  // Get UserName

                        // Fetch additional user details
                        String[] userDetails = fetchUserDetails(email, connection);

                        // Return role, username, and additional user details
                        return new String[]{role, username, userDetails[0], userDetails[1], userDetails[2], userDetails[3]};
                    } else {
                        return new String[]{"invalid"};
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new String[]{"error"};  // Database or SQL error
                }
            } else {
                return new String[]{"error"};  // Connection failure
            }
        }

        // Fetch additional user details
        private String[] fetchUserDetails(String email, Connection connection) {
            String[] details = new String[4]; // Array to hold AccountNumber, IDNumber, PhoneNumber, Email
            String query = "SELECT AccountNumber, IDNumber, PhoneNumber, Email FROM AspNetUsers WHERE Email = ?";
            try {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    details[0] = resultSet.getString("AccountNumber");
                    details[1] = resultSet.getString("IDNumber");
                    details[2] = resultSet.getString("PhoneNumber");
                    details[3] = resultSet.getString("Email");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return details; // Return fetched details
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result.length == 1) {  // Handle errors or invalid login
                if (result[0].equals("invalid")) {
                    Toast.makeText(MainActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_LONG).show();
                } else if (result[0].equals("error")) {
                    Toast.makeText(MainActivity.this, "Database connection error.", Toast.LENGTH_LONG).show();
                }
            } else {  // Handle successful login
                String role = result[0];
                String username = result[1];
                String accountNumber = result[2];
                String idNumber = result[3];
                String phoneNumber = result[4];
                String email = result[5];

                // Pass email, username, role, and additional details to the next activity
                Intent intent;
                if (role.equals("Staff") || role.equals("Student") || role.equals("User")) {
                    intent = new Intent(MainActivity.this, user_activity.class);
                } else if (role.equals("Consultant")) {
                    intent = new Intent(MainActivity.this, UserTransaction.class); // or ConsultantActivity, FinancialAdvisorActivity
                } else {
                    return; // Handle other roles if necessary
                }
                // Pass data to the next activity
                intent.putExtra("email", email);
                intent.putExtra("username", username);
                intent.putExtra("role", role);
                intent.putExtra("accountNumber", accountNumber);
                intent.putExtra("idNumber", idNumber);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }
        }
    }
}
