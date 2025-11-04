// InventoryService.java
package com.beyourshelf.service.inventory;

import java.util.List;
import java.util.Map;

import com.beyourshelf.model.dao.book.IBookDAO;
import com.beyourshelf.model.entity.Book;
import com.beyourshelf.model.entity.CartItem;

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
     * Reserve stock for the books in the given map. Each entry contains a book and
     * the quantity to reserve.
     *
     * @param books Map of Book to quantity requested for reservation.
     * @return true if all books have enough stock, false otherwise.
     */
    public boolean reserveBooks(Map<Book, Integer> books) {
        // Attempt conditional reductions per item; roll back if any fail.
        java.util.List<Map.Entry<Book, Integer>> reduced = new java.util.ArrayList<>();
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            int bookId = entry.getKey().getBookId();
            int quantity = entry.getValue();
            boolean ok = bookDAO.reducePhysicalCopies(bookId, quantity);
            if (!ok) {
                // Roll back any previously reduced items
                for (Map.Entry<Book, Integer> prev : reduced) {
                    bookDAO.increasePhysicalCopies(prev.getKey().getBookId(), prev.getValue());
                }
                return false;
            }
            reduced.add(entry);
        }
        return true;
    }

    /**
     * Revert reservations for a list of cart items, restoring the stock for each
     * book.
     *
     * @param cartItems List of CartItem objects representing the items to revert.
     */
    public void revertReservations(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            // Add back physical copies based on cart item quantity
            bookDAO.increasePhysicalCopies(item.getBookId(), item.getQuantity());
        }
    }

    /**
     * Finalize stock adjustments for books after successful payment, updating both
     * physical stock and sold copies.
     *
     * @param books Map of Book to quantity sold.
     */
    public void finalizeStockAdjustments(Map<Book, Integer> books) {
        // Physical copies should already be reserved; only update sold copies here.
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            int bookId = entry.getKey().getBookId();
            int quantityPurchased = entry.getValue();
            bookDAO.updateSoldCopies(bookId, quantityPurchased);
            System.out.println("Finalized stock adjustments for book ID " + bookId + ": " +
                    quantityPurchased + " copies sold.");
        }
    }
}
