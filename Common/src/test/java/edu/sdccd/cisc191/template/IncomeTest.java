package edu.sdccd.cisc191.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

public class IncomeTest {

    @Test
    public void testGetType() {
        Income income = new Income(1, "Income 1", "Income", 500.0, new Timestamp(System.currentTimeMillis()));

        String type = income.getType();
        Assertions.assertEquals("Income", type);
    }
}