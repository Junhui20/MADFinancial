package com.uccd3223.madfinancial;

public class Transaction {
    private String id;
    private String type;
    private double amount;
    private String name;
    private String category;
    private String categoryName;
    private String categoryColor;
    private long timestamp;
    private String description;
    private boolean isRecurring;
    private String recurringPeriod;
    private String imageUri;

    public Transaction() {
        // Required empty constructor
    }

    public Transaction(String id, String type, double amount, String name,
                      String category, String categoryName, String categoryColor,
                      long timestamp, String description, boolean isRecurring,
                      String recurringPeriod, String imageUri) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.category = category;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.timestamp = timestamp;
        this.description = description;
        this.isRecurring = isRecurring;
        this.recurringPeriod = recurringPeriod;
        this.imageUri = imageUri;
    }

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getCategoryName() { return categoryName; }
    public String getCategoryColor() { return categoryColor; }
    public long getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
    public boolean isRecurring() { return isRecurring; }
    public String getRecurringPeriod() { return recurringPeriod; }
    public String getImageUri() { return imageUri; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setCategoryColor(String categoryColor) { this.categoryColor = categoryColor; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setDescription(String description) { this.description = description; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }
    public void setRecurringPeriod(String recurringPeriod) { this.recurringPeriod = recurringPeriod; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}
