package com.kelompokganas.financeapp;

public class Transaction {
    private int id;
    private String title;
    private String type;
    private double amount;
    private String category;
    private String notes;
    private String date;

    public Transaction(int id, String title, String type, double amount, String category, String notes, String date) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.notes = notes;
        this.date = date;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getNotes() { return notes; }
    public String getDate() { return date; }
}