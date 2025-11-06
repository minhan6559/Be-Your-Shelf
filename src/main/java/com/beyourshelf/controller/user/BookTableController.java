package com.beyourshelf.controller.user;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.util.List;
import java.util.Optional;

import com.beyourshelf.model.entity.Book;
import com.beyourshelf.model.entity.CartItem;
import com.beyourshelf.model.entity.ShoppingCart;
import com.beyourshelf.service.ServiceManager;
import com.beyourshelf.service.book.IBookService;
import com.beyourshelf.service.cart.ICartService;
import com.beyourshelf.utils.ui.UIUtils;

/**
 * Abstract controller for managing book tables in user views.
 * Handles displaying books, adding them to the shopping cart, and managing
 * book-related actions.
 * This controller is meant to be extended by specific controllers that display
 * book lists.
 */
public abstract class BookTableController {

    // The user's shopping cart
    protected ShoppingCart shoppingCart;

    // Services for handling books and cart-related operations
    protected IBookService bookService = ServiceManager.getInstance().getBookService();
    protected ICartService cartService = ServiceManager.getInstance().getCartService(); // Retrieve cartService through
                                                                                        // ServiceManager

    // UI elements for displaying books in a table
    protected TableView<Book> booksTableView;
    protected TableColumn<Book, String> titleColumn;
    protected TableColumn<Book, String> authorColumn;
    protected TableColumn<Book, String> priceColumn;
    protected TableColumn<Book, Integer> stockColumn;
    protected TableColumn<Book, Integer> soldCopiesColumn;
    protected TableColumn<Book, Button> actionColumn;

    /**
     * Initializes the table with book data and columns.
     * Sets up the column mappings and populates the action column with buttons.
     *
     * @param tableView        The TableView to display the books.
     * @param titleColumn      The column for book titles.
     * @param authorColumn     The column for book authors.
     * @param priceColumn      The column for book prices.
     * @param stockColumn      The column for available stock.
     * @param soldCopiesColumn The column for sold copies.
     * @param actionColumn     The column for the "Add to Cart" buttons.
     */
    protected void initializeBookTable(TableView<Book> tableView, TableColumn<Book, String> titleColumn,
            TableColumn<Book, String> authorColumn,
            TableColumn<Book, String> priceColumn, TableColumn<Book, Integer> stockColumn,
            TableColumn<Book, Integer> soldCopiesColumn, TableColumn<Book, Button> actionColumn) {
        this.booksTableView = tableView;
        this.titleColumn = titleColumn;
        this.authorColumn = authorColumn;
        this.priceColumn = priceColumn;
        this.stockColumn = stockColumn;
        this.soldCopiesColumn = soldCopiesColumn;
        this.actionColumn = actionColumn;

        // Set cell value factories for columns
        titleColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAuthor()));
        priceColumn.setCellValueFactory(
                data -> new SimpleObjectProperty<>(String.format("$%.2f", data.getValue().getPrice())));
        stockColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(
                bookService.findBookById(data.getValue().getBookId()).getPhysicalCopies()));
        soldCopiesColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSoldCopies()));

        // Populate action column with "Add to Cart" buttons
        actionColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(createAddButton(data.getValue())));

        loadBooks(); // Load books (to be implemented in subclasses)
    }

    /**
     * Creates an "Add to Cart" button for each book row in the table.
     *
     * @param book The book associated with the button.
     * @return The button to add the book to the cart.
     */
    private Button createAddButton(Book book) {
        Button addButton = new Button("Add to Cart");
        addButton.setStyle("-fx-background-color: #d2691e; " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 5 15; " +
                "-fx-font-size: 14px;");

        // Ensure that button clicks don't interfere with row selection
        addButton.setOnAction(event -> {
            handleAddButton(book);
            event.consume(); // Prevents the event from propagating to the row selection
        });

        // Add hover effect to button
        addButton.setOnMouseEntered(_ -> addButton.setStyle(
                "-fx-background-color: #ff7f50; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-size: 14px;"));
        addButton.setOnMouseExited(_ -> addButton.setStyle(
                "-fx-background-color: #d2691e; -fx-text-fill: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-size: 14px;"));

        return addButton;
    }

    /**
     * Handles the "Add" button action, showing a dialog to input the quantity, and
     * adding the book to the cart.
     *
     * @param book The book to be added to the cart.
     */
    private void handleAddButton(Book book) {
        Optional<String> result = showQuantityDialog();
        result.ifPresent(quantityStr -> {
            try {
                int quantity = parseQuantity(quantityStr);
                validateStockAndAddToCart(book, quantity);
                UIUtils.showAlert("Success", quantity + " copies added to your cart.");
            } catch (IllegalArgumentException e) {
                UIUtils.showError("Invalid Input", e.getMessage());
            }
        });
    }

    /**
     * Parses the input quantity and ensures it is a valid number greater than zero.
     *
     * @param quantityStr The quantity string input by the user.
     * @return The parsed quantity as an integer.
     * @throws IllegalArgumentException if the input is invalid.
     */
    private int parseQuantity(String quantityStr) throws IllegalArgumentException {
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0)
                throw new IllegalArgumentException("Quantity must be greater than 0.");
            return quantity;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid number.");
        }
    }

    /**
     * Validates the stock availability and adds the book to the shopping cart if
     * valid.
     *
     * @param book     The book to be added.
     * @param quantity The quantity of the book.
     * @throws IllegalArgumentException if there is insufficient stock or other
     *                                  validation issues.
     */
    private void validateStockAndAddToCart(Book book, int quantity) throws IllegalArgumentException {
        if (shoppingCart == null) {
            throw new IllegalArgumentException("Shopping cart is not initialized.");
        }

        Book latestBook = bookService.findBookById(book.getBookId());
        int availableStock = latestBook.getPhysicalCopies();
        if (availableStock < quantity) {
            throw new IllegalArgumentException("Only " + availableStock + " copies available.");
        }

        updateCart(book, quantity);
    }

    /**
     * Updates the shopping cart with the selected book and quantity, and
     * synchronizes the cart with the database.
     *
     * @param book     The book to be added.
     * @param quantity The quantity of the book.
     */
    private void updateCart(Book book, int quantity) {
        shoppingCart.addBook(book, quantity);
        int cartId = cartService.getOrCreateCart(shoppingCart.getUserId());
        cartService.addOrUpdateBookInCart(cartId, book.getBookId(), quantity);

        // Sync the shopping cart with the latest items
        syncCartWithDatabase(cartId);
    }

    /**
     * Synchronizes the shopping cart with the database, ensuring the latest items
     * are reflected in the cart.
     *
     * @param cartId The ID of the shopping cart in the database.
     */
    private void syncCartWithDatabase(int cartId) {
        List<CartItem> updatedCartItems = cartService.getCartItems(cartId);
        shoppingCart.clearCart();
        for (CartItem item : updatedCartItems) {
            Book cartBook = bookService.findBookById(item.getBookId());
            shoppingCart.addBook(cartBook, item.getQuantity());
        }
    }

    /**
     * Shows a dialog for the user to input the quantity of books to add to the
     * cart.
     *
     * @return An Optional containing the user's input, or an empty Optional if the
     *         dialog was cancelled.
     */
    private Optional<String> showQuantityDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Quantity");
        dialog.setHeaderText("How many copies of the book would you like to add?");
        dialog.setContentText("Quantity:");
        return dialog.showAndWait();
    }

    /**
     * Abstract method for loading books into the table.
     * Subclasses must implement this method to define how books are loaded into the
     * table.
     */
    protected abstract void loadBooks();

    /**
     * Sets the shopping cart for the controller.
     *
     * @param shoppingCart The user's shopping cart.
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}
