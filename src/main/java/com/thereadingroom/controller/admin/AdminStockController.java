package com.thereadingroom.controller.admin;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;

/**
 * Controller for managing book stocks in the admin panel.
 * Provides functionality to view, search, edit, add, and update books' stock.
 */
public class AdminStockController {

    @FXML
    private TableView<Book> bookTableView;  // Table view to display books

    @FXML
    private TableColumn<Book, Integer> bookIdColumn;  // Column for book ID

    @FXML
    private TableColumn<Book, String> bookTitleColumn;  // Column for book title

    @FXML
    private TableColumn<Book, String> bookAuthorColumn;  // Column for book author

    @FXML
    private TableColumn<Book, Integer> bookStockColumn;  // Column for book stock

    @FXML
    private TableColumn<Book, Double> bookPriceColumn;  // Column for book price

    @FXML
    private TableColumn<Book, Integer> bookSoldCopiesColumn;  // Column for sold copies

    @FXML
    private TableColumn<Book, Void> actionColumn;  // Column for edit and remove actions

    @FXML
    private TextField searchField;  // Text field for searching books

    @FXML
    private TextField bookIdField;  // Text field for inputting the book ID for stock updates

    @FXML
    private TextField newStockField;  // Text field for inputting new stock value

    // Service for handling book-related operations
    private final IBookService bookService = ServiceManager.getInstance().getBookService();

    /**
     * Initializes the controller by setting up the table and loading books.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        addActionButtonsToTable();
        loadBooks();
        UIUtils.loadCSS(bookTableView, "/com/thereadingroom/css/table-style.css");
    }

    /**
     * Configures the table columns to map to book properties.
     */
    private void setupTableColumns() {
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookStockColumn.setCellValueFactory(new PropertyValueFactory<>("physicalCopies"));
        bookPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        bookSoldCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("soldCopies"));
    }

    /**
     * Loads all books from the service and displays them in the table.
     */
    private void loadBooks() {
        List<Book> books = bookService.getAllBooks();
        bookTableView.getItems().setAll(books);
    }

    /**
     * Adds Edit and Remove buttons to each row in the table.
     */
    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Book, Void> call(final TableColumn<Book, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = createEditButton();
                    private final Button removeButton = createRemoveButton();

                    private Button createEditButton() {
                        Button button = new Button("Edit");
                        button.setStyle("-fx-background-color: #d2691e; -fx-text-fill: white; -fx-border-radius: 15; -fx-padding: 8 15;");
                        button.setOnAction(_ -> {
                            Book book = getTableView().getItems().get(getIndex());
                            handleEditBook(book);
                        });
                        return button;
                    }

                    private Button createRemoveButton() {
                        Button button = new Button("Remove");
                        button.setStyle("-fx-background-color: #ff4500; -fx-text-fill: white; -fx-border-radius: 15; -fx-padding: 8 15;");
                        button.setOnAction(_ -> {
                            Book book = getTableView().getItems().get(getIndex());
                            handleRemoveBook(book);
                        });
                        return button;
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox actionButtons = new HBox(editButton, removeButton);
                            actionButtons.setSpacing(10); // Set spacing between the buttons
                            actionButtons.setAlignment(Pos.CENTER); // Align buttons in the center
                            setGraphic(actionButtons);
                        }
                    }
                };
            }
        });
    }

    /**
     * Handles the Edit Book action by loading the edit modal for the selected book.
     *
     * @param book The selected book to edit.
     */
    private void handleEditBook(Book book) {
        UIUtils.loadModal("/com/thereadingroom/fxml/admin/admin_edit_book.fxml", "Edit Book", controller -> {
            AdminEditBookController editBookController = (AdminEditBookController) controller;
            editBookController.setBook(book);
        }, (Stage) bookTableView.getScene().getWindow());
        loadBooks();
    }

    /**
     * Handles the Remove Book action by confirming the removal and deleting the book.
     *
     * @param book The selected book to remove.
     */
    private void handleRemoveBook(Book book) {
        boolean confirm = UIUtils.showConfirmation("Confirm Deletion", "Are you sure you want to remove the book?");
        if (confirm) {
            boolean success = bookService.deleteBookById(book.getBookId());
            if (success) {
                UIUtils.showAlert("Success", "Book removed successfully!");
                loadBooks();  // Reload the book list after deletion
            } else {
                UIUtils.showError("Error", "Failed to remove book.");
            }
        }
    }

    /**
     * Handles the search functionality by filtering books based on the search keyword.
     */
    @FXML
    public void handleSearchBooks() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            List<Book> searchResults = bookService.searchBooksByTitle(keyword);
            bookTableView.getItems().setAll(searchResults);
        } else {
            loadBooks();  // Reload all books if the search field is empty
        }
    }

    /**
     * Handles updating the stock of a specific book based on the entered Book ID and new stock value.
     * Directly adjusts the stock (replaces the value rather than adding or subtracting).
     */
    @FXML
    public void handleUpdateStock() {
        try {
            int bookId = Integer.parseInt(bookIdField.getText().trim());
            int newStock = Integer.parseInt(newStockField.getText().trim());

            if (newStock < 0) {
                UIUtils.showError("Invalid Input", "Stock value cannot be negative.");
                return;
            }

            boolean success = bookService.updatePhysicalCopies(bookId, newStock);
            if (success) {
                UIUtils.showAlert("Success", "Stock updated successfully!");
                loadBooks();  // Reload the book list after stock update
            } else {
                UIUtils.showError("Update Failed", "Could not update stock. Please check the Book ID.");
            }
        } catch (NumberFormatException e) {
            UIUtils.showError("Invalid Input", "Please enter valid numbers for Book ID and Stock.");
        }
    }

    /**
     * Opens the Add Book modal for adding a new book.
     */
    @FXML
    public void handleAddBook() {
        UIUtils.loadModal("/com/thereadingroom/fxml/admin/admin_add_book.fxml", "Add New Book", controller -> {
            AdminAddBookController addBookController = (AdminAddBookController) controller;
            addBookController.setOnBookAdded(this::loadBooks);  // Pass the reload callback
        }, (Stage) bookTableView.getScene().getWindow());
    }
}
