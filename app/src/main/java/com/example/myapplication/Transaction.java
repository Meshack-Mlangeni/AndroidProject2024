package com.example.myapplication;

import java.util.Date;

public class Transaction {
    private int id;
    private String message;
    private Date notificationDate; // Assuming this is the date of the transaction
    private boolean isRead;
    private String userEmail;

    // Constructor
    public Transaction(int id, String message, Date notificationDate, boolean isRead, String userEmail) {
        this.id = id;
        this.message = message;
        this.notificationDate = notificationDate;
        this.isRead = isRead;
        this.userEmail = userEmail;
    }

    // Getters
    public int getId() { return id; }
    public String getMessage() { return message; }
    public Date getNotificationDate() { return notificationDate; }
    public boolean isRead() { return isRead; }
    public String getUserEmail() { return userEmail; }
}
