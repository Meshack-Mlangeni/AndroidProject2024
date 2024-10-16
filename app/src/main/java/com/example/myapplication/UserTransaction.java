package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserTransaction extends AppCompatActivity {

    private ConnectHelper connectHelper;
    private ListView listView;
    private ArrayList<String> transactionsList;
    private String userEmail;  // Store the passed email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction);

        // Initialize ConnectHelper and ListView
        connectHelper = new ConnectHelper(this);
        listView = findViewById(R.id.listView);
        transactionsList = new ArrayList<>();

        // Retrieve the email from the previous intent
        userEmail = getIntent().getStringExtra("email");

        if (userEmail != null && !userEmail.isEmpty()) {
            // Execute the query in the background
            new FetchTransactionsTask(userEmail).execute();
        } else {
            Toast.makeText(UserTransaction.this, "No email passed from previous activity", Toast.LENGTH_LONG).show();
        }
    }

    // AsyncTask to perform database operations in the background
    private class FetchTransactionsTask extends AsyncTask<Void, Void, Boolean> {

        private String userEmail;

        // Constructor to pass user email
        public FetchTransactionsTask(String userEmail) {
            this.userEmail = userEmail;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = connectHelper.getConnection();
            if (connection != null) {
                // SQL query to get transactions where UserEmail = userEmail
                String query = "SELECT BankAccountIdSender, BankAccountIdReceiver, Amount, TransactionDate, Reference, UserEmail " +
                        "FROM Transactions WHERE UserEmail = '" + userEmail + "'";
                ResultSet resultSet = connectHelper.getData(query);

                try {
                    // Clear the previous list
                    transactionsList.clear();

                    // Loop through the ResultSet and add the transactions to the list
                    while (resultSet.next()) {
                        String bankAccountIdSender = resultSet.getString("BankAccountIdSender");
                        String bankAccountIdReceiver = resultSet.getString("BankAccountIdReceiver");
                        String amount = resultSet.getString("Amount");
                        String transactionDate = resultSet.getString("TransactionDate");
                        String reference = resultSet.getString("Reference");
                        String userEmail = resultSet.getString("UserEmail");

                        String transaction = "Sender: " + bankAccountIdSender +
                                ", Receiver: " + bankAccountIdReceiver +
                                ", Amount: " + amount +
                                ", Date: " + transactionDate +
                                ", Reference: " + reference +
                                ", Email: " + userEmail;

                        transactionsList.add(transaction);
                    }
                    resultSet.close();
                    connection.close();
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Create an ArrayAdapter to display the transactions in the ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        UserTransaction.this,
                        android.R.layout.simple_list_item_1,
                        transactionsList
                );
                listView.setAdapter(adapter);
            } else {
                Toast.makeText(UserTransaction.this, "Failed to retrieve data from database", Toast.LENGTH_LONG).show();
            }
        }
    }
}
