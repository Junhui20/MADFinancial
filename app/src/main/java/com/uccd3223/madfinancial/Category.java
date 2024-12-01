package com.uccd3223.madfinancial;

public class Category {
    private String id;
    private String name;
    private String type; // "income" or "expense"
    private int iconResource;
    private String color;

    public Category() {
        // Required empty constructor for Firebase
    }

    public Category(String id, String name, String type, int iconResource, String color) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.iconResource = iconResource;
        this.color = color;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getIconResource() { return iconResource; }
    public void setIconResource(int iconResource) { this.iconResource = iconResource; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
