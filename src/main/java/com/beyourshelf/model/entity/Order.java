package com.beyourshelf.model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Represents a customer's order, including the order details such as order
 * number,
 * user information, total price, and the list of ordered items.
 */
public class Order {

    private int orderId; // Unique identifier for the order
    private String orderNumber; // Unique order number for the transaction
    private int userId; // The ID of the user who placed the order
    private double totalPrice; // Total price of the order
    private LocalDateTime orderDate; // Date and time when the order was placed
    private List<OrderItem> orderItems; // List of items included in the order

    /**
     * Constructor to create an Order instance.
     *
     * @param orderNumber The unique order number
     * @param userId      The ID of the user placing the order
     * @param totalPrice  The total price of the order
     * @param orderItems  The list of items included in the order
     */
    public Order(String orderNumber, int userId, double totalPrice, List<OrderItem> orderItems) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderItems = orderItems;
        this.orderDate = LocalDateTime.now(); // Automatically sets the current date and time
    }

    public Order() {
        // No-argument constructor
    }

    // Getters and setters for order fields

    // Returns the unique order ID
    public int getOrderId() {
        return orderId;
    }

    // Sets the unique order ID
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    // Returns the unique order number
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    // Returns the user ID associated with the order
    public int getUserId() {
        return userId;
    }

    // Sets the user ID for the order
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Returns the total price of the order
    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Returns the date when the order was placed
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    // Returns the list of items in the order
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    // Sets the list of items in the order
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // Sets the order date (useful for updating or retrieving order history)
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Helper method to format the order date in a readable format (MM/dd/yyyy
     * HH:mm).
     *
     * @return The formatted order date as a string.
     */
    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        return orderDate.format(formatter);
    }
}
