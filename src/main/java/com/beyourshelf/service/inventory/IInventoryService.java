package com.beyourshelf.service.inventory;

import java.util.List;
import java.util.Map;

import com.beyourshelf.model.entity.Book;
import com.beyourshelf.model.entity.CartItem;

/**
 * IInventoryService defines the operations related to managing book inventory,
 * including reserving books, reverting reservations, and finalizing stock
 * adjustments.
 */
public interface IInventoryService {

    /**
     * Reserve books from the inventory based on the provided map of books and their
     * quantities.
     *
     * @param books Map containing Book objects and their corresponding quantities
     *              to reserve.
     * @return true if all books have sufficient stock for reservation, false
     *         otherwise.
     */
    boolean reserveBooks(Map<Book, Integer> books);

    /**
     * Revert previously reserved stock for the provided cart items. This is useful
     * in case of payment failure or cancellation.
     *
     * @param cartItems List of CartItem objects representing the items to revert.
     */
    void revertReservations(List<CartItem> cartItems);

    /**
     * Finalize stock adjustments for the purchased books, reducing stock and
     * updating the number of sold copies.
     *
     * @param books Map of Book objects and their corresponding quantities sold.
     */
    void finalizeStockAdjustments(Map<Book, Integer> books);
}
