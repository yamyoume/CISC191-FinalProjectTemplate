package edu.sdccd.cisc191.template;

import java.sql.Timestamp;

public abstract class Entry {
    int id;
    String description;
    String type;
    Double amount;
    Timestamp date;

    abstract String getType();

    int getId() {return id;}
    String getDescription() {
        return description;
    }
    String getAmount() {
        return ""+amount;
    }

    Timestamp getDate() { return date;}
}
