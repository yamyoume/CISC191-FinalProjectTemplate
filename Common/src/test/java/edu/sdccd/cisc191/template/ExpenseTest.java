package edu.sdccd.cisc191.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

public class ExpenseTest {

    @Test
    public void testGetType() {
        Expense expense = new Expense(1, "Expense 1", "Expense", 100.0, new Timestamp(System.currentTimeMillis()));

        String type = expense.getType();
        Assertions.assertEquals("Expense", type);
    }
}