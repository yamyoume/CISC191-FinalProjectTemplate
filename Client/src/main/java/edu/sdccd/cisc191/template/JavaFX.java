package edu.sdccd.cisc191.template;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;

import static java.lang.Double.parseDouble;

import java.util.Date;
import java.util.Optional;

import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

public class JavaFX extends Application {
    // this is used to pause 0.5 seconds before the search function
    private PauseTransition pauseTransition;
    static Book book = new Book();

    /**
     * @param book The main book class
     * @param grid The Entries GridPane
     * @param summeryGrid The Summery GridPane
     * @param entries The list of entries
     * @param renderSummery Boolean to control whether to update the summery or not
     * This method does basic JavaFX stuff
     */
    public void renderEntry(Book book, GridPane grid, GridPane summeryGrid, ArrayList<Entry> entries, Boolean renderSummery) {
        grid.getChildren().clear();
        grid.add(new Label("Description"), 0,0);
        grid.add(new Label("Type"), 1,0);
        grid.add(new Label("Amount"), 2,0);
        grid.add(new Label("Date/Time"), 3,0);

        for (int i=0; i < entries.size(); i++) {
            String descriptionString = entries.get(i).getDescription();
            Label descriptionLabel = new Label(descriptionString);
            grid.add(descriptionLabel,0, i+1);

            String typeString = entries.get(i).getType();
            Label typeLabel = new Label(typeString);
            grid.add(typeLabel,1, i+1);

            String amountString = entries.get(i).getAmount();
            Label amountLabel = new Label("$"+String.valueOf(amountString));
            grid.add(amountLabel,2, i+1);

            Timestamp dateString = entries.get(i).getDate();
            Label dateLabel = new Label(String.valueOf(dateString).substring(0, String.valueOf(dateString).length() - 5));
            grid.add(dateLabel,3, i+1);

            Button editButton = new Button("Edit");
            Entry toEdit = entries.get(i);
            editButton.setOnAction(e -> openEditEntryWindow(e, toEdit, grid,summeryGrid, entries));
            grid.add(editButton, 5, i+1);

            Button deleteButton = new Button("Delete");
            Entry toDelete = entries.get(i);
            deleteButton.setOnAction(e -> openDeleteEntry(e, toDelete, grid,summeryGrid, entries));
            grid.add(deleteButton, 6, i+1);
        }
        if (renderSummery) {
            // render Summery all the time except when doing a search (set in the search function to 0)
            // or when switching what entries to Show using the "Show Entries" dropdown list
            renderSummery(book, summeryGrid);
        }
    }

    /**
     * @param book The main book class
     * @param summeryGrid The Summery GridPane
     * This method does basic JavaFX stuff
     */
    public void renderSummery(Book book, GridPane summeryGrid) {
        summeryGrid.getChildren().clear();
        summeryGrid.add(new Label("SUMMERY"), 2,0);
        summeryGrid.add(new Label("Total Income:"), 2,1);
        Label totalIncome = new Label("$"+String.valueOf(book.getTotalIncome()));
        summeryGrid.add(totalIncome, 3,1);
        summeryGrid.add(new Label("Total Expenses:"), 2,2);
        Label totalExpenses = new Label("$"+String.valueOf(book.getTotalExpenses()));
        summeryGrid.add(totalExpenses, 3,2);
        summeryGrid.add(new Label("Net Profit:"), 2,3);
        Label netProfit = new Label("$"+String.valueOf(book.getNetProfit()));
        summeryGrid.add(netProfit, 3,3);
    }

    /**
     * @param jsonData JSON Data came from the server (Server.java)
     * @param search String field for searching the database
     * @throws Exception
     * This function goes one of two ways, if it's the first time initializing it uses the JSON data that came from the server and uses it to
     * input entries in the entries list and then render
     * the second scenario is to make a call to the mysql database and selects all the entries or if the search parameter is present then it uses
     * that search string to look for something specific  in the database (Fuzzy Search)
     */
    public static void initializeEntries(Optional<String> jsonData, Optional<String> search) throws Exception {
        if (jsonData.isPresent()) {
            String input = jsonData.get();
            // Parse the JSON response
            JSONArray jsonArray = new JSONArray(input);

            // Loop through the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Access the properties of each JSON object
                int id = jsonObject.getInt("id");
                String description = jsonObject.getString("description");
                String type = jsonObject.getString("type");
                double amount = jsonObject.getInt("amount");
                Timestamp date = Timestamp.valueOf(jsonObject.getString("date"));

                // Initializing Entries from JSON
                if (type.equals("Income")) {
                    book.addEntry(new Income(id, description, type, amount, date));
                } else {
                    book.addEntry(new Expense(id, description, type, amount, date));
                }

            }
        } else {
            Connection con = Database.getConnection();
            String sql = "SELECT * FROM entries";
            if (search.isPresent()) { // If there is a Search Term
                sql += " WHERE `description` LIKE ? OR `type` LIKE ? OR `amount` LIKE ? OR `date` LIKE ?";
            }
            PreparedStatement ps = con.prepareStatement(sql);
            if (search.isPresent()) { // If there is a Search Term
                ps.setString(1, "%" +  search.get() + "%");
                ps.setString(2, "%" +  search.get() + "%");
                ps.setString(3, "%" +  search.get() + "%");
                ps.setString(4, "%" +  search.get() + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("type").equals("Income")) {
                    book.addEntry(new Income(rs.getInt("id"), rs.getString("description"), "Income", rs.getDouble("amount"), rs.getTimestamp("date")));
                } else {
                    book.addEntry(new Expense(rs.getInt("id"), rs.getString("description"), "Expense", rs.getDouble("amount"), rs.getTimestamp("date")));
                }
            }
        }
    }

    public void start(Stage stage) throws Exception {

        ArrayList<Entry> entries = book.getEntries();

        GridPane headerGrid = new GridPane();
        headerGrid.setAlignment(Pos.TOP_LEFT);
        headerGrid.setHgap(10);
        headerGrid.setVgap(10);
        headerGrid.setPadding(new Insets(5,5,5,25));

        GridPane entryGrid = new GridPane();
        entryGrid.setAlignment(Pos.TOP_LEFT);
        entryGrid.setHgap(10);
        entryGrid.setVgap(10);
        entryGrid.setPadding(new Insets(5,5,5,25));

        GridPane actionGrid = new GridPane();
        actionGrid.setAlignment(Pos.TOP_LEFT);
        actionGrid.setHgap(10);
        actionGrid.setVgap(10);
        actionGrid.setPadding(new Insets(30,5,5,25));

        GridPane summeryGrid = new GridPane();
        summeryGrid.setAlignment(Pos.TOP_RIGHT);
        summeryGrid.setHgap(10);
        summeryGrid.setVgap(10);
        summeryGrid.setPadding(new Insets(30,5,5,25));

        /*
         **********************
         * Search Button and function
         **********************
         */
        Label searchLabel = new Label("Search");
        headerGrid.add(searchLabel, 0,0);
        TextField searchInput = new TextField();
        headerGrid.add(searchInput,1,0);
        // Create a PauseTransition with a delay of 0.5 seconds
        pauseTransition = new PauseTransition(Duration.seconds(0.5));
        pauseTransition.setOnFinished(e -> {
            String text = searchInput.getText();
            if (text.length() >= 2) {
                // Perform the search when at least two characters are input
                entries.clear();
                try {
                    initializeEntries(Optional.empty(), Optional.of(text));
                    renderEntry(book, entryGrid,summeryGrid, entries, false);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            } else if (text.length() == 0) {
                // get all entries
                entries.clear();
                try {
                    initializeEntries(Optional.empty(), Optional.empty());
                    renderEntry(book, entryGrid,summeryGrid, entries, true);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Add a listener to the TextField on key released
        searchInput.setOnKeyReleased(e -> {
            // Restart the PauseTransition on each key released event
            pauseTransition.playFromStart();
        });
        /*
         **********************
         * Search Button and function END
         **********************
         */


        /*
         **********************
         * Action GridPane
         **********************
         */
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().add("Expenses");
        comboBox.getItems().add("Incomes");
        comboBox.getItems().add("Both");
        comboBox.setValue("Both");
        ComboBox sortComboBox = new ComboBox();
        sortComboBox.getItems().add("Description");
        sortComboBox.getItems().add("Amount Low-to-High");
        sortComboBox.getItems().add("Amount High-to-Low");
        sortComboBox.getItems().add("Date");
        sortComboBox.setValue("Date");

        EventHandler<ActionEvent> event = e -> {
            ArrayList<Entry> entries1 = null;
            if (comboBox.getValue() == "Expenses") {
                entries1 = book.getExpenses();
            }
            if (comboBox.getValue() == "Incomes") {
                entries1 = book.getIncome();
            }
            if (comboBox.getValue() == "Both") {
                entries1 = book.getEntries();
            }
            book.sort(sortComboBox.getValue().toString());
            entryGrid.getChildren().clear();
            renderEntry(book, entryGrid, summeryGrid, entries1, false);

        };


        actionGrid.add(new Label("ACTIONS"), 0, 0);
        actionGrid.add(new Label("Show Entries:"), 0 ,1);

        comboBox.setOnAction(event);
        actionGrid.add(comboBox, 1, 1);

        actionGrid.add(new Label("Sort Criteria: "), 0,2);
        sortComboBox.setOnAction(event);
        actionGrid.add(sortComboBox, 1, 2);

        Button openAddEntryButton = new Button("Add Entry");
        actionGrid.add(openAddEntryButton, 0, 5);
        // Set an event handler for the Add Entry button
        openAddEntryButton.setOnAction(e -> {
            // Call a method to open a new window
            openAddEntryWindow(entryGrid,summeryGrid, entries);
        });

        /*
         **********************
         * Action GridPane END
         **********************
         */


        // Rendering the entries initially
        renderEntry(book, entryGrid,summeryGrid, entries, true);


        VBox vbox = new VBox(headerGrid, entryGrid, actionGrid);
        VBox vbox2 = new VBox(actionGrid, summeryGrid);
        HBox hBox = new HBox(vbox, vbox2);
        Scene scene = new Scene(hBox, 850, 600);
        stage.setTitle("Expenses/Income System");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param entryGrid The Add Entry GridPane
     * @param summeryGrid The Summery GridPane
     * @param entries The list of entries
     * This method opens up a new window to allow the user to input a new Entry
     */
    private void openAddEntryWindow(GridPane entryGrid, GridPane summeryGrid, ArrayList<Entry> entries) {
        Stage addEntryWindow = new Stage();
        addEntryWindow.setTitle("Add Entry");

        // Create the content for the new window

        GridPane actionGrid = new GridPane();
        actionGrid.setAlignment(Pos.TOP_LEFT);
        actionGrid.setHgap(10);
        actionGrid.setVgap(10);
        actionGrid.setPadding(new Insets(5,5,5,5));

        actionGrid.add(new Label ("Description:"), 0, 1);
        TextField descriptionInput = new TextField();
        actionGrid.add(descriptionInput,1,1);

        actionGrid.add(new Label("Type:"),0,2);
        ComboBox typeComboBox = new ComboBox();
        typeComboBox.getItems().add("Expense");
        typeComboBox.getItems().add("Income");
        typeComboBox.setValue("Expense");
        actionGrid.add(typeComboBox,1,2);

        actionGrid.add(new Label("Amount:"),0,3);
        TextField amountInput = new TextField();
        actionGrid.add(amountInput,1,3);

        EventHandler<ActionEvent> saveEntry = e -> {
            java.util.Date date = new Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

            try {
                Connection con = Database.getConnection();
                String insertSql = "INSERT INTO entries (description, type, amount, date) VALUES (?,?,?,?)";
                PreparedStatement ips = con.prepareStatement(insertSql);
                ips.setString(1, descriptionInput.getText());
                ips.setString(2, typeComboBox.getValue().toString());
                ips.setDouble(3, parseDouble(amountInput.getText()));
                ips.setTimestamp(4, timestamp);
                ips.executeUpdate();
//                JOptionPane.showMessageDialog(null, "Saved!");
                System.out.println("finished insert");
            } catch (Exception ex) {
                ex.printStackTrace();
//                JOptionPane.showMessageDialog(null, "Error");
            }
            entries.clear();
            try {
                initializeEntries(Optional.empty(),Optional.empty());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            addEntryWindow.close();
            entryGrid.getChildren().clear();
            renderEntry(book, entryGrid,summeryGrid, entries, true);
        };

        Button addEntryButton = new Button("Save");
        Button closeAddEntry = new Button("Close");
        addEntryButton.setOnAction(saveEntry);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().add(addEntryButton);
        hbox.getChildren().add(closeAddEntry);
        closeAddEntry.setOnAction(e -> {
            // Close the new window
            addEntryWindow.close();
        });

        VBox vbox = new VBox(actionGrid, hbox);


        // Set the content on the new window
        Scene scene = new Scene(vbox, 300, 160);
        addEntryWindow.setScene(scene);

        // Show the new window
        addEntryWindow.show();

    }

    /**
     * @param event Edit click event (not used)
     * @param toEdit Which button was clicked meaning which Entry
     * @param entryGrid The Add Entry GridPane
     * @param summeryGrid The Summery GridPane
     * @param entries The list of entries
     * This method opens up a new window for editing an Entry, it also sends an update sql statement to the Database if the user chose to save
     */
    private void openEditEntryWindow(ActionEvent event, Entry toEdit, GridPane entryGrid, GridPane summeryGrid, ArrayList<Entry> entries) {
        // Getting data from that Entry for edit
        Entry entry = book.getEntry(toEdit);

        Stage editEntryWindow = new Stage();
        editEntryWindow.setTitle("Edit Entry");

        // Create the content for the new window

        GridPane actionGrid = new GridPane();
        actionGrid.setAlignment(Pos.TOP_LEFT);
        actionGrid.setHgap(10);
        actionGrid.setVgap(10);
        actionGrid.setPadding(new Insets(5,5,5,5));

        actionGrid.add(new Label ("Description:"), 0, 1);
        TextField descriptionInput = new TextField();
        descriptionInput.setText(entry.description);
        actionGrid.add(descriptionInput,1,1);

        actionGrid.add(new Label("Type:"),0,2);
        ComboBox typeComboBox = new ComboBox();
        typeComboBox.getItems().add("Expense");
        typeComboBox.getItems().add("Income");
        typeComboBox.setValue(entry.type);
        actionGrid.add(typeComboBox,1,2);

        actionGrid.add(new Label("Amount:"),0,3);
        TextField amountInput = new TextField();
        amountInput.setText(entry.amount.toString());
        actionGrid.add(amountInput,1,3);

        EventHandler<ActionEvent> updateEntry = e -> {
            java.util.Date date = new Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

            try {
                Connection con = Database.getConnection();
                String insertSql = "UPDATE entries SET description = ?, type = ?, amount = ? WHERE id = ?";
                PreparedStatement ips = con.prepareStatement(insertSql);
                ips.setString(1, descriptionInput.getText());
                ips.setString(2, typeComboBox.getValue().toString());
                ips.setDouble(3, parseDouble(amountInput.getText()));
                ips.setInt(4, entry.id);
                ips.executeUpdate();
//                JOptionPane.showMessageDialog(null, "Saved!");
                System.out.println("finished update");
            } catch (Exception ex) {
                ex.printStackTrace();
//                JOptionPane.showMessageDialog(null, "Error");
            }
            entries.clear();
            try {
                initializeEntries(Optional.empty(),Optional.empty());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            editEntryWindow.close();
            entryGrid.getChildren().clear();
            renderEntry(book, entryGrid,summeryGrid, entries, true);
        };

        Button updateEntryButton = new Button("Update");
        Button closeEditEntry = new Button("Close");
        updateEntryButton.setOnAction(updateEntry);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().add(updateEntryButton);
        hbox.getChildren().add(closeEditEntry);
        closeEditEntry.setOnAction(e -> {
            // Close the new window
            editEntryWindow.close();
        });

        VBox vbox = new VBox(actionGrid, hbox);


        // Set the content on the new window
        Scene scene = new Scene(vbox, 300, 160);
        editEntryWindow.setScene(scene);

        // Show the new window
        editEntryWindow.show();

    }

    /**
     * @param event Delete click event (not used)
     * @param toDelete Which button was clicked meaning which Entry
     * @param entryGrid The Add Entry GridPane
     * @param summeryGrid The Summery GridPane
     * @param entries The list of entries
     * This method opens a new window that asks the user "Are you sure you want to delete" for confirmation.
     * It sends a delete sql statement to the database if the user chose to delete
     */
    private void openDeleteEntry(ActionEvent event, Entry toDelete, GridPane entryGrid, GridPane summeryGrid, ArrayList<Entry> entries) {
        // Getting data from that Entry for edit
        Entry entry = book.getEntry(toDelete);

        Stage deleteEntryWindow = new Stage();
        deleteEntryWindow.setTitle("Delete Entry");

        // Create the content for the new window

        EventHandler<ActionEvent> deleteEntry = e -> {
            java.util.Date date = new Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

            try {
                Connection con = Database.getConnection();
                String insertSql = "DELETE FROM entries WHERE id = ?";
                PreparedStatement ips = con.prepareStatement(insertSql);
                ips.setInt(1, entry.id);
                ips.executeUpdate();
//                JOptionPane.showMessageDialog(null, "Saved!");
                System.out.println("finished delete");
            } catch (Exception ex) {
                ex.printStackTrace();
//                JOptionPane.showMessageDialog(null, "Error");
            }
            entries.clear();
            try {
                initializeEntries(Optional.empty(),Optional.empty());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            deleteEntryWindow.close();
            entryGrid.getChildren().clear();
            renderEntry(book, entryGrid,summeryGrid, entries, true);
        };

        Button deleteEntryButton = new Button("Yes");
        Button closeDeleteEntry = new Button("No");
        deleteEntryButton.setOnAction(deleteEntry);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().add(deleteEntryButton);
        hbox.getChildren().add(closeDeleteEntry);
        closeDeleteEntry.setOnAction(e -> {
            // Close the new window
            deleteEntryWindow.close();
        });

        VBox vbox = new VBox(new Label("Are you sure?"), hbox);
        vbox.setAlignment(Pos.CENTER);


        // Set the content on the new window
        Scene scene = new Scene(vbox, 300, 60);
        deleteEntryWindow.setScene(scene);

        // Show the new window
        deleteEntryWindow.show();

    }


    public static void main(String[] args) throws Exception {
        initializeEntries(Optional.of(args[0]),Optional.empty());
        launch(args);
    }
}
