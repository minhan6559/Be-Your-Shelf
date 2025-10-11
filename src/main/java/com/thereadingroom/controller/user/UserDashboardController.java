package com.thereadingroom.controller.user;

import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.ShoppingCart;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Controller class for managing the user dashboard.
 * It displays the top 5 best-selling books and provides an interface for the user to interact with the shopping cart.
 */
public class UserDashboardController extends BookTableController {

    @FXML
    private Label welcomeLabel;  // Label to display a personalized welcome message

    @FXML
    private TableView<Book> topBooksTableView;  // TableView for displaying the top-selling books

    @FXML
    private TableColumn<Book, String> topTitleColumn;  // Column for the book titles

    @FXML
    private TableColumn<Book, String> topAuthorColumn;  // Column for the book authors

    @FXML
    private TableColumn<Book, String> topPriceColumn;  // Column for the book prices

    @FXML
    private TableColumn<Book, Integer> topStockColumn;  // Column for the available stock of the books

    @FXML
    private TableColumn<Book, Integer> topSoldCopiesColumn;  // Column for the number of sold copies

    @FXML
    private TableColumn<Book, Button> topActionColumn;  // Column for action buttons (like "Add to Cart")

    private IBookService bookService;  // Service for handling book-related operations

    /**
     * Initializes the controller by setting up the table and loading the top-selling books.
     * Called automatically after the FXML fields are injected.
     */
    @FXML
    public void initialize() {
        // Initialize the book service using the ServiceManager
        bookService = ServiceManager.getInstance().getBookService();

        UIUtils.loadCSS(topBooksTableView, "/com/thereadingroom/css/table-style.css");

        // Disable column resizing and apply a fixed policy
        topBooksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Initialize the book table with the necessary columns
        initializeBookTable(topBooksTableView, topTitleColumn, topAuthorColumn, topPriceColumn, topStockColumn, topSoldCopiesColumn, topActionColumn);
    }

    /**
     * Loads the top 5 best-selling books into the table.
     * This method is overridden from the parent BookTableController class.
     */
    @Override
    protected void loadBooks() {
        // Retrieve the top 5 best-selling books from the book service
        List<Book> topBooks = bookService.getTop5Books();

        // Update the table view with the retrieved books
        if (booksTableView != null) {
            booksTableView.getItems().setAll(topBooks);
        }
    }

    /**
     * Sets the logged-in user's first and last name and updates the welcome message accordingly.
     *
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     */
    public void setUser(String firstName, String lastName) {
        // Update the welcome message with the user's name
        updateWelcomeMessage(firstName, lastName);
    }

    /**
     * Updates the welcome label with a personalized message.
     *
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     */
    private void updateWelcomeMessage(String firstName, String lastName) {
        // Ensure that the welcome label and user details are not null
        if (welcomeLabel != null && firstName != null && lastName != null) {
            // Set the welcome message in the label
            welcomeLabel.setText("Welcome, " + firstName + " " + lastName + " to The Reading Room!");
        } else {
            // Log an error if the welcome label or user information is null
            System.err.println("Error: welcomeLabel or user information is null.");
        }
    }

    /**
     * Sets the user's shopping cart and passes it to the parent class for further handling.
     *
     * @param shoppingCart The user's shopping cart.
     */
    @Override
    public void setShoppingCart(ShoppingCart shoppingCart) {
        // Ensure the shopping cart is not null
        if (shoppingCart != null) {
            // Pass the shopping cart to the parent class for further processing
            super.setShoppingCart(shoppingCart);
        } else {
            // Throw an exception if the shopping cart is null
            throw new IllegalArgumentException("Shopping cart cannot be null");
        }
    }
}
