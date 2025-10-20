package com.thereadingroom.model.entity;

/**
 * Represents an item in the shopping cart, including the book ID and quantity.
 * Each CartItem corresponds to a specific book in the cart.
 */
public class CartItem {
    private final int bookId; // Unique identifier for the associated book, does not change
    private int quantity; // Quantity of the book in the cart

    /**
     * Constructor to create a CartItem with the necessary details.
     *
     * @param bookId   Unique identifier for the associated book
     * @param quantity The quantity of the book in the cart
     */
    public CartItem(int bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getter for the quantity of the book in the cart
    public int getQuantity() {
        return quantity;
    }

    // Setter for the quantity of the book in the cart
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter for the unique identifier of the associated book
    public int getBookId() {
        return bookId;
    }
}
