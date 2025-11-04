package com.beyourshelf.controller.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

import com.beyourshelf.model.entity.Book;
import com.beyourshelf.service.ServiceManager;
import com.beyourshelf.service.book.IBookService;
import com.beyourshelf.utils.ui.UIUtils;

/**
 * Controller class for viewing and interacting with all available books.
 * It provides a table view to display books and allows users to search for
 * specific books by title.
 */
public class ViewAllBooksController extends BookTableController {

    private IBookService bookService; // Service for managing book-related operations

    @FXML
    private TableView<Book> allBooksTableView; // Table view to display all books

    @FXML
    private TableColumn<Book, String> allTitleColumn; // Column for book titles

    @FXML
    private TableColumn<Book, String> allAuthorColumn; // Column for book authors

    @FXML
    private TableColumn<Book, String> allPriceColumn; // Column for book prices

    @FXML
    private TableColumn<Book, Integer> allStockColumn; // Column for available book stock

    @FXML
    private TableColumn<Book, Integer> allSoldCopiesColumn; // Column for the number of sold copies

    @FXML
    private TableColumn<Book, Button> allActionColumn; // Column for action buttons (like "Add to Cart")

    @FXML
    private TextField searchField; // Text field for searching books by title

    /**
     * Initializes the controller by setting up the table and loading all books.
     * This method is called automatically after the FXML fields are injected.
     */
    @FXML
    public void initialize() {
        // Initialize the book service using the ServiceManager
        bookService = ServiceManager.getInstance().getBookService();

        // Initialize the book table with the necessary columns
        initializeBookTable(allBooksTableView, allTitleColumn, allAuthorColumn, allPriceColumn, allStockColumn,
                allSoldCopiesColumn, allActionColumn);

        UIUtils.loadCSS(allBooksTableView, "/com/beyourshelf/css/table-style.css");

        // Disable column resizing and apply a fixed policy
        allBooksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load all available books when the controller is initialized
        loadBooks();
    }

    /**
     * Loads all available books into the table.
     * This method is overridden from the parent BookTableController class.
     */
    @Override
    protected void loadBooks() {
        if (allBooksTableView != null) { // Ensure the table view is initialized
            // Fetch all books using the book service
            List<Book> allBooks = bookService.getAllBooks();

            // Update the table view with the retrieved books
            booksTableView.getItems().setAll(allBooks);
        } else {
            // Log an error if the table view is null
            System.err.println("Error: allBooksTableView is null.");
        }
    }

    /**
     * Handles the book search functionality.
     * It filters books based on the title entered in the search field.
     */
    @FXML
    public void handleSearchBooks() {
        if (booksTableView != null) { // Ensure the table view is initialized
            // Get the keyword from the search field
            String keyword = searchField != null ? searchField.getText().trim() : "";

            if (!keyword.isEmpty()) {
                // If a keyword is provided, search for books by title
                List<Book> searchResults = bookService.searchBooksByTitle(keyword);
                booksTableView.getItems().setAll(searchResults); // Update the table with search results
            } else {
                // If no keyword is provided, reload all books
                loadBooks();
            }
        } else {
            // Log an error if the table view or search field is null
            System.err.println("Error: booksTableView or searchField is null.");
        }
    }
}
