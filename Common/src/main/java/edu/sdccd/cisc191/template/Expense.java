package edu.sdccd.cisc191.template;

import java.sql.Timestamp;

public class Expense extends Entry {

    public Expense (int id, String description, String type, double amount, Timestamp date) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }
    @Override
    String getType() {
        return "Expense";
    }
}
