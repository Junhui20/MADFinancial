package com.uccd3223.madfinancial;

public class Category {
    private String id;
    private String type;
    private String name;
    private String color;
    private int iconResourceId;

    public Category(String id, String type, String name, String color) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.color = color;
        this.iconResourceId = getIconResourceForCategory(name.toLowerCase());
    }

    private int getIconResourceForCategory(String category) {
        switch (category.toLowerCase()) {
            case "food":
                return R.drawable.ic_food;
            case "transport":
                return R.drawable.ic_transport;
            case "shopping":
                return R.drawable.ic_shopping;
            case "bills":
                return R.drawable.ic_bills;
            case "entertainment":
                return R.drawable.ic_entertainment;
            case "health":
                return R.drawable.ic_health;
            case "education":
                return R.drawable.ic_education;
            case "housing":
                return R.drawable.ic_housing_new;
            case "insurance":
                return R.drawable.ic_insurance_new;
            case "tax":
                return R.drawable.ic_tax;
            case "utilities":
                return R.drawable.ic_utilities_new;
            case "groceries":
                return R.drawable.ic_food;
            case "personal care":
                return R.drawable.ic_personal_new;
            case "pets":
                return R.drawable.ic_others;
            case "gifts":
                return R.drawable.ic_gift;
            case "travel":
                return R.drawable.ic_travel_new;
            case "electronics":
                return R.drawable.ic_electronics_new;
            case "fitness":
                return R.drawable.ic_fitness_new;
            case "salary":
                return R.drawable.ic_salary;
            case "bonus":
                return R.drawable.ic_bonus;
            case "investment":
                return R.drawable.ic_investment;
            case "gift":
                return R.drawable.ic_gift;
            case "interest":
                return R.drawable.ic_interest;
            case "rental":
                return R.drawable.ic_rental;
            default:
                return R.drawable.ic_others;
        }
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }
}
