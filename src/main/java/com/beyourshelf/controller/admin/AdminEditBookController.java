package com.beyourshelf.controller.admin;

import com.beyourshelf.model.entity.Book;
import com.beyourshelf.service.ServiceManager;
import com.beyourshelf.service.book.IBookService;
import com.beyourshelf.utils.ui.UIUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for the "Edit Book" functionality in the admin panel.
 * This class allows the admin to modify the details of an existing book and
 * update it in the database through the IBookService.
 */
public class AdminEditBookController {

    @FXML
    private TextField titleField; // Input field for the book title

    @FXML
    private TextField authorField; // Input field for the book author

    @FXML
    private TextField stockField; // Input field for the number of physical copies (stock)

    @FXML
    private TextField priceField; // Input field for the price of the book

    @FXML
    private TextField soldCopiesField; // Input field for the number of sold copies

    @FXML
    private Label feedbackLabel; // Label to display success or error messages to the user

    // Book service instance for handling book-related operations
    private final IBookService bookService = ServiceManager.getInstance().getBookService();

    private Book book; // Holds the book object that is being edited

    /**
     * Set the book to be edited. This method is called to pass the selected book's
     * details to the controller, which then populates the form fields with the
     * book's current information.
     *
     * @param book The book to be edited.
     */
    public void setBook(Book book) {
        this.book = book;
        // Populate the form fields with the current book information
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        stockField.setText(String.valueOf(book.getPhysicalCopies()));
        priceField.setText(String.valueOf(book.getPrice()));
        soldCopiesField.setText(String.valueOf(book.getSoldCopies()));
    }

    /**
     * Handles saving the updated book information. It validates the input fields,
     * updates the book entity, and calls the service to persist the changes.
     */
    @FXML
    public void handleSaveBook() {
        try {
            // Update the book entity with the new values from the input fields
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setPhysicalCopies(Integer.parseInt(stockField.getText())); // Set physical copies directly
            book.setPrice(Double.parseDouble(priceField.getText())); // Set price
            book.setSoldCopies(Integer.parseInt(soldCopiesField.getText())); // Set sold copies

            // Call the service to update the book in the database
            boolean success = bookService.updateBook(book);

            // Provide feedback and close the window
            if (success) {
                UIUtils.showAlert("Success", "Book updated successfully!");
                UIUtils.closeCurrentWindow(titleField); // Close the window
            } else {
                feedbackLabel.setText("Failed to update book.");
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Invalid input for stock, price, or sold copies.");
        }
    }

    /**
     * Handles the cancel button action. It closes the window without saving any
     * changes.
     */
    @FXML
    public void handleCancel() {
        UIUtils.closeCurrentWindow(titleField); // Close the window without saving changes
    }
}
