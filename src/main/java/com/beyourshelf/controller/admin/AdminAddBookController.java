package com.beyourshelf.controller.admin;

import com.beyourshelf.model.entity.Book;
import com.beyourshelf.service.ServiceManager;
import com.beyourshelf.service.book.IBookService;
import com.beyourshelf.utils.ui.UIUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for the "Add Book" functionality in the admin panel.
 * This class manages the form inputs for adding a new book, validates the data,
 * and interacts with the IBookService to persist the new book in the database.
 */
public class AdminAddBookController {

    @FXML
    private TextField titleField; // Input field for the book title

    @FXML
    private TextField authorField; // Input field for the book author

    @FXML
    private TextField stockField; // Input field for the number of physical copies (stock)

    @FXML
    private TextField priceField; // Input field for the price of the book

    @FXML
    private Label feedbackLabel; // Label to display success or error messages to the user

    // Book service instance for handling book-related operations
    private final IBookService bookService = ServiceManager.getInstance().getBookService();

    // Reference to the parent controller to trigger reload of books
    private Runnable onBookAdded;

    /**
     * Handles the action of adding a new book to the system.
     */
    @FXML
    public void handleAddBook() {
        try {
            // Get the values from the input fields
            String title = titleField.getText();
            String author = authorField.getText();
            int stock = Integer.parseInt(stockField.getText()); // Convert stock to integer
            double price = Double.parseDouble(priceField.getText()); // Convert price to double

            // Ensure all fields are filled
            if (title.isEmpty() || author.isEmpty()) {
                feedbackLabel.setText("Please fill in all fields."); // Display an error message
                return;
            }

            // Create a new Book object with default sold copies set to 0
            Book newBook = new Book(0, title, author, stock, price, 0);

            // Attempt to add the book using the book service
            boolean success = bookService.addBook(newBook);

            // Check the result of the add operation and provide feedback
            if (success) {
                UIUtils.showAlert("Success", "Book added successfully!");
                // Reload the book list after adding a new book
                if (onBookAdded != null) {
                    onBookAdded.run(); // Trigger the reload of books in AdminStockController
                }
                UIUtils.closeCurrentWindow(titleField); // Close the window
            } else {
                feedbackLabel.setText("Failed to add book."); // Display failure message
            }
        } catch (NumberFormatException e) {
            // Handle invalid input for stock or price fields
            feedbackLabel.setText("Invalid input for stock or price.");
        }
    }

    /**
     * Sets a callback to be executed after a book is successfully added.
     * 
     * @param onBookAdded The callback that triggers book reloading.
     */
    public void setOnBookAdded(Runnable onBookAdded) {
        this.onBookAdded = onBookAdded;
    }

    /**
     * Handles the cancel button action. Closes the current window without saving.
     */
    @FXML
    public void handleCancel() {
        UIUtils.closeCurrentWindow(titleField); // Close the window without saving
    }
}
