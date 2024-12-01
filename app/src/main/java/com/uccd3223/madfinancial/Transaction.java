package com.uccd3223.madfinancial;

import java.util.Date;

public class Transaction {
    private String id;
    private double amount;
    private String type;
    private String category;
    private String description;
    private Date date;
    private String currency;

    public Transaction() {} // Required for Firestore

    public Transaction(String id, double amount, String type, String category, String description, Date date, String currency) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description;
        this.date = date;
        this.currency = currency;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
