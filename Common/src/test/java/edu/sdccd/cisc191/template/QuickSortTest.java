package edu.sdccd.cisc191.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;

public class QuickSortTest {
    @Test
    public void testQuickSortByDescription() {
        ArrayList<Entry> entries = new ArrayList<>();
        Entry entry1 = new Expense(1, "Expense C", "Expense", 100.0, new Timestamp(System.currentTimeMillis()));
        Entry entry2 = new Income(2, "Income A", "Income", 200.0, new Timestamp(System.currentTimeMillis()));
        Entry entry3 = new Expense(3, "Expense B", "Expense", 300.0, new Timestamp(System.currentTimeMillis()));
        entries.add(entry1);
        entries.add(entry2);
        entries.add(entry3);

        QuickSort.quickSort(entries, "Description", 0, entries.size() - 1);

        Assertions.assertEquals("Expense B", entries.get(0).getDescription());
        Assertions.assertEquals("Expense C", entries.get(1).getDescription());
        Assertions.assertEquals("Income A", entries.get(2).getDescription());
    }

    @Test
    public void testQuickSortByAmountLowToHigh() {
        ArrayList<Entry> entries = new ArrayList<>();
        Entry entry1 = new Expense(1, "Expense 1", "Expense", 300.0, new Timestamp(System.currentTimeMillis()));
        Entry entry2 = new Income(2, "Income 1", "Income", 100.0, new Timestamp(System.currentTimeMillis()));
        Entry entry3 = new Expense(3, "Expense 2", "Expense", 200.0, new Timestamp(System.currentTimeMillis()));
        entries.add(entry1);
        entries.add(entry2);
        entries.add(entry3);

        QuickSort.quickSort(entries, "Amount Low-to-High", 0, entries.size() - 1);

        Assertions.assertEquals("Income 1", entries.get(0).getDescription());
        Assertions.assertEquals("Expense 2", entries.get(1).getDescription());
        Assertions.assertEquals("Expense 1", entries.get(2).getDescription());
    }

    @Test
    public void testQuickSortByAmountHighToLow() {
        ArrayList<Entry> entries = new ArrayList<>();
        Entry entry1 = new Expense(1, "Expense 1", "Expense", 300.0, new Timestamp(System.currentTimeMillis()));
        Entry entry2 = new Income(2, "Income 1", "Income", 100.0, new Timestamp(System.currentTimeMillis()));
        Entry entry3 = new Expense(3, "Expense 2", "Expense", 200.0, new Timestamp(System.currentTimeMillis()));
        entries.add(entry1);
        entries.add(entry2);
        entries.add(entry3);

        QuickSort.quickSort(entries, "Amount High-to-Low", 0, entries.size() - 1);

        Assertions.assertEquals("Expense 1", entries.get(0).getDescription());
        Assertions.assertEquals("Expense 2", entries.get(1).getDescription());
        Assertions.assertEquals("Income 1", entries.get(2).getDescription());
    }

    @Test
    public void testQuickSortByDate() {
        ArrayList<Entry> entries = new ArrayList<>();
        Entry entry1 = new Expense(1, "Expense 1", "Expense", 100.0, new Timestamp(System.currentTimeMillis()));
        Entry entry2 = new Income(2, "Income 1", "Income", 200.0, new Timestamp(System.currentTimeMillis() + 1000));
        Entry entry3 = new Expense(3, "Expense 2", "Expense", 300.0, new Timestamp(System.currentTimeMillis() - 1000));
        entries.add(entry1);
        entries.add(entry2);
        entries.add(entry3);

        QuickSort.quickSort(entries, "Date", 0, entries.size() - 1);

        Assertions.assertEquals("Expense 2", entries.get(0).getDescription());
        Assertions.assertEquals("Expense 1", entries.get(1).getDescription());
        Assertions.assertEquals("Income 1", entries.get(2).getDescription());
    }
}
