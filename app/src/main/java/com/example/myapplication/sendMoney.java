package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class sendMoney extends AppCompatActivity {

    private EditText etSenderAccount, etReceiverAccount, etAmount, etAvailableBalance;
    private Button btnTransfer, btnGoBack;
    private ConnectHelper connectHelper;
    private String userEmail; // Assuming userEmail is passed to this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        // Initialize views
        etSenderAccount = findViewById(R.id.et_sender_account);
        etReceiverAccount = findViewById(R.id.et_receiver_account);
        etAmount = findViewById(R.id.et_amount);
        etAvailableBalance = findViewById(R.id.et_available_balance);
        btnTransfer = findViewById(R.id.btn_transfer);
        btnGoBack = findViewById(R.id.btn_go_back);

        // Assuming you receive the user email from the previous activity
        userEmail = getIntent().getStringExtra("email");

        // Initialize database connection
        connectHelper = new ConnectHelper(this);

        // Load sender's account details
        loadSenderAccount();

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferMoney();
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close current activity
            }
        });
    }

    private void loadSenderAccount() {
        String query = "SELECT TOP 1 [AccountNumber], [Balance] FROM [db_aae335_bankdb].[dbo].[BankAccounts] WHERE [UserEmail] = '" + userEmail + "'";
        ResultSet resultSet = connectHelper.getData(query);
        try {
            if (resultSet != null && resultSet.next()) {
                String accountNumber = resultSet.getString("AccountNumber");
                double balance = resultSet.getDouble("Balance");
                etSenderAccount.setText(accountNumber);
                etAvailableBalance.setText(String.valueOf(balance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void transferMoney() {
        String receiverAccount = etReceiverAccount.getText().toString();
        String amountString = etAmount.getText().toString();

        if (receiverAccount.isEmpty() || amountString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountString);
        double availableBalance = Double.parseDouble(etAvailableBalance.getText().toString());

        // Check if the amount is valid
        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount > availableBalance) {
            Toast.makeText(this, "Insufficient funds", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update sender's balance
        String updateSenderBalanceQuery = "UPDATE [db_aae335_bankdb].[dbo].[BankAccounts] SET [Balance] = [Balance] - " + amount + " WHERE [UserEmail] = '" + userEmail + "'";
        boolean isSenderUpdated = connectHelper.insertData(updateSenderBalanceQuery);

        // Update receiver's balance
        String updateReceiverBalanceQuery = "UPDATE [db_aae335_bankdb].[dbo].[BankAccounts] SET [Balance] = [Balance] + " + amount + " WHERE [AccountNumber] = '" + receiverAccount + "'";
        boolean isReceiverUpdated = connectHelper.insertData(updateReceiverBalanceQuery);

        if (isSenderUpdated && isReceiverUpdated) {
            // Insert transaction record
            String insertTransactionQuery = "INSERT INTO [db_aae335_bankdb].[dbo].[Transactions] ([BankAccountIdSender], [BankAccountIdReceiver], [Amount], [TransactionDate], [UserEmail]) " +
                    "VALUES ((SELECT [Id] FROM [db_aae335_bankdb].[dbo].[BankAccounts] WHERE [UserEmail] = '" + userEmail + "'), " +
                    "(SELECT [Id] FROM [db_aae335_bankdb].[dbo].[BankAccounts] WHERE [AccountNumber] = '" + receiverAccount + "'), " +
                    amount + ", GETDATE(), '" + userEmail + "')";
            connectHelper.insertData(insertTransactionQuery);

            // Insert notification for sender
            String insertSenderNotification = "INSERT INTO [db_aae335_bankdb].[dbo].[Notifications] ([Message], [NotificationDate], [UserEmail]) " +
                    "VALUES ('Transaction of " + amount + " to " + receiverAccount + " was successful', GETDATE(), '" + userEmail + "')";
            connectHelper.insertData(insertSenderNotification);

            // Insert notification for receiver
            String insertReceiverNotification = "INSERT INTO [db_aae335_bankdb].[dbo].[Notifications] ([Message], [NotificationDate], [UserEmail]) " +
                    "VALUES ('You received " + amount + " from " + etSenderAccount.getText().toString() + "', GETDATE(), '" + receiverAccount + "')";
            connectHelper.insertData(insertReceiverNotification);
            Intent i = new Intent(sendMoney.this,Successfully.class);
            startActivity(i);
            Toast.makeText(this, "Transaction successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
        }
    }
}
