package com.thereadingroom.model.dao.cart;

import com.thereadingroom.model.entity.CartItem;

import java.util.List;

/**
 * Interface for managing cart-related data access operations.
 * Defines the methods for interacting with the cart in the database, including adding/removing books,
 * updating quantities, managing stock reservations, and clearing carts.
 */
public interface ICartDAO {

    /**
     * Retrieves or creates a cart for the specified user.
     * If the user has no active cart, a new one will be created.
     *
     * @param userId the user ID
     * @return the cart ID (existing or newly created)
     */
    int getOrCreateCart(int userId);

    /**
     * Updates the quantity of a book in the cart.
     *
     * @param cartId the cart ID
     * @param bookId the book ID
     * @param quantity the new quantity to set
     */
    void updateBookQuantity(int cartId, int bookId, int quantity);

    /**
     * Adds or updates a book in the cart.
     * If the book already exists in the cart, its quantity will be updated.
     *
     * @param cartId the cart ID
     * @param bookId the book ID
     * @param quantity the quantity to add or update
     */
    void addOrUpdateBookInCart(int cartId, int bookId, int quantity);

    /**
     * Retrieves all items from the specified cart.
     *
     * @param cartId the cart ID
     * @return a list of CartItem objects representing the items in the cart
     */
    List<CartItem> getCartItems(int cartId);

    /**
     * Removes a specific book from the cart.
     *
     * @param cartId the cart ID
     * @param bookId the book ID to remove
     */
    void removeBookFromCart(int cartId, int bookId);
}
