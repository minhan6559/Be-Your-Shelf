// InventoryService.java
package com.thereadingroom.service.inventory;

import com.thereadingroom.model.dao.book.IBookDAO;
import com.thereadingroom.model.entity.Book;
import com.thereadingroom.model.entity.CartItem;

import java.util.List;
import java.util.Map;

/**
 * InventoryService manages book inventory operations such as reserving stock,
 * reverting reservations, and finalizing stock adjustments after purchases.
 */
public class InventoryService implements IInventoryService {

    // Dependency: Data access object for book operations
    private final IBookDAO bookDAO;

    // Constructor to inject IBookDAO dependency
    public InventoryService(IBookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Reserve stock for the books in the given map. Each entry contains a book and the quantity to reserve.
     *
     * @param books Map of Book to quantity requested for reservation.
     * @return true if all books have enough stock, false otherwise.
     */
    public boolean reserveBooks(Map<Book, Integer> books) {
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            int availableCopies = bookDAO.getAvailableCopies(entry.getKey());  // Check stock availability
            if (availableCopies < entry.getValue()) {
                return false; // Not enough stock for at least one book
            }
            // Reserve the stock by reducing physical copies
            bookDAO.reducePhysicalCopies(entry.getKey().getBookId(), entry.getValue());
        }
        return true;  // All books reserved successfully
    }

    /**
     * Revert reservations for a list of cart items, restoring the stock for each book.
     *
     * @param cartItems List of CartItem objects representing the items to revert.
     */
    public void revertReservations(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            // Restore physical copies based on cart item quantity
            bookDAO.updatePhysicalCopies(item.getBookId(), item.getQuantity());
        }
    }

    /**
     * Finalize stock adjustments for books after successful payment, updating both physical stock and sold copies.
     *
     * @param books Map of Book to quantity sold.
     */
    public void finalizeStockAdjustments(Map<Book, Integer> books) {
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            int bookId = entry.getKey().getBookId();  // Get book ID
            int quantityPurchased = entry.getValue(); // Get quantity purchased

            // Step 1: Decrease physical copies by quantity purchased
            bookDAO.reducePhysicalCopies(bookId, quantityPurchased);

            // Step 2: Increase sold copies by quantity purchased
            bookDAO.updateSoldCopies(bookId, quantityPurchased);

            // Log the final stock adjustment for each book
            System.out.println("Finalized stock adjustments for book ID " + bookId + ": " +
                    quantityPurchased + " copies sold.");
        }
    }
}
