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

public class UserNotifications extends AppCompatActivity {

    private ConnectHelper connectHelper;
    private ListView listView;
    private ArrayList<String> notificationsList;
    private String userEmail;  // Store the passed email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notifications);

        // Initialize ConnectHelper and ListView
        connectHelper = new ConnectHelper(this);
        listView = findViewById(R.id.listView1);
        notificationsList = new ArrayList<>();

        // Retrieve the email from the previous intent
        userEmail = getIntent().getStringExtra("email");

        if (userEmail != null && !userEmail.isEmpty()) {
            // Execute the query in the background
            new FetchNotificationsTask(userEmail).execute();
        } else {
            Toast.makeText(UserNotifications.this, "No email passed from previous activity", Toast.LENGTH_LONG).show();
        }
    }

    // AsyncTask to perform database operations in the background
    private class FetchNotificationsTask extends AsyncTask<Void, Void, Boolean> {

        private String userEmail;

        // Constructor to pass user email
        public FetchNotificationsTask(String userEmail) {
            this.userEmail = userEmail;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = connectHelper.getConnection();
            if (connection != null) {
                // SQL query to get top 1000 notifications for the user
                String query = "SELECT  Id, Message, NotificationDate, IsRead, UserEmail " +
                        "FROM Notifications WHERE UserEmail = '" + userEmail + "'";
                ResultSet resultSet = connectHelper.getData(query);

                try {
                    // Clear the previous list
                    notificationsList.clear();

                    // Loop through the ResultSet and add the notifications to the list
                    while (resultSet.next()) {
                        String id = resultSet.getString("Id");
                        String message = resultSet.getString("Message");
                        String notificationDate = resultSet.getString("NotificationDate");
                        String isRead = resultSet.getString("IsRead");
                        String userEmail = resultSet.getString("UserEmail");

                        String notification = "ID: " + id +
                                ", Message: " + message +
                                ", Date: " + notificationDate +
                                ", Is Read: " + isRead +
                                ", Email: " + userEmail;

                        notificationsList.add(notification);
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
                // Create an ArrayAdapter to display the notifications in the ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        UserNotifications.this,
                        android.R.layout.simple_list_item_1,
                        notificationsList
                );
                listView.setAdapter(adapter);
            } else {
                Toast.makeText(UserNotifications.this, "Failed to retrieve data from database", Toast.LENGTH_LONG).show();
            }
        }
    }
}
