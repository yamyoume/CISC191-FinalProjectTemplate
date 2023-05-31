package edu.sdccd.cisc191.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import java.sql.Timestamp;
import java.util.ArrayList;

public class BookTest {

    @Test
    public void testAddEntry() {
        Book book = new Book();
        Entry entry = new Expense(1, "Expense 1", "Expense", 100.0, new Timestamp(System.currentTimeMillis()));

        book.addEntry(entry);

        ArrayList<Entry> entries = book.getEntries();
        Assertions.assertEquals(1, entries.size());
        Assertions.assertEquals(entry, entries.get(0));
    }

    @Test
    public void testRemoveEntry() {
        Book book = new Book();
        Entry entry1 = new Expense(1, "Expense 1", "Expense", 100.0, new Timestamp(System.currentTimeMillis()));
        Entry entry2 = new Income(2, "Income 1", "Income", 200.0, new Timestamp(System.currentTimeMillis()));

        book.addEntry(entry1);
        book.addEntry(entry2);
        book.removeEntry(entry1);

        ArrayList<Entry> entries = book.getEntries();
        Assertions.assertEquals(1, entries.size());
        Assertions.assertEquals(entry2, entries.get(0));
    }

    @Test
    public void testGetTotalExpenses() {
        Book book = new Book();
        Entry expense1 = new Expense(1, "Expense 1", "Expense", 100.0, new Timestamp(System.currentTimeMillis()));
        Entry expense2 = new Expense(2, "Expense 2", "Expense", 200.0, new Timestamp(System.currentTimeMillis()));

        book.addEntry(expense1);
        book.addEntry(expense2);

        double totalExpenses = book.getTotalExpenses();
        Assertions.assertEquals(300.0, totalExpenses, 0.001);
    }

    @Test
    public void testGetTotalIncome() {
        Book book = new Book();
        Entry income1 = new Income(1, "Income 1", "Income", 500.0, new Timestamp(System.currentTimeMillis()));
        Entry income2 = new Income(2, "Income 2", "Income", 300.0, new Timestamp(System.currentTimeMillis()));

        book.addEntry(income1);
        book.addEntry(income2);

        double totalIncome = book.getTotalIncome();
        Assertions.assertEquals(800.0, totalIncome, 0.001);
    }

    @Test
    public void testGetNetProfit() {
        Book book = new Book();
        Entry income1 = new Income(1, "Income 1", "Income", 500.0, new Timestamp(System.currentTimeMillis()));
        Entry expense1 = new Expense(2, "Expense 1", "Expense", 200.0, new Timestamp(System.currentTimeMillis()));

        book.addEntry(income1);
        book.addEntry(expense1);

        double netProfit = book.getNetProfit();
        Assertions.assertEquals(300.0, netProfit, 0.001);
    }
}
