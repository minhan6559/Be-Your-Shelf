package com.beyourshelf.service;

import com.beyourshelf.model.dao.book.BookDAO;
import com.beyourshelf.model.dao.book.IBookDAO;
import com.beyourshelf.service.CSVExport.CSVExportService;
import com.beyourshelf.service.CSVExport.ICSVExportService;
import com.beyourshelf.service.book.BookService;
import com.beyourshelf.service.book.IBookService;
import com.beyourshelf.service.cart.CartService;
import com.beyourshelf.service.cart.ICartService;
import com.beyourshelf.service.inventory.IInventoryService;
import com.beyourshelf.service.inventory.InventoryService;
import com.beyourshelf.service.order.IOrderService;
import com.beyourshelf.service.order.OrderService;
import com.beyourshelf.service.payment.IPaymentService;
import com.beyourshelf.service.payment.PaymentService;
import com.beyourshelf.service.user.IUserService;
import com.beyourshelf.service.user.UserService;

public class ServiceManager {

    // Singleton instance of ServiceManager
    private static ServiceManager instance;

    // Services managed by ServiceManager
    private final IUserService userService; // Handles user-related operations
    private final IOrderService orderService; // Handles order-related operations
    private final IInventoryService inventoryService; // Handles inventory-related operations
    private final IPaymentService paymentService; // Handles payment processing
    private final ICartService cartService; // Manages shopping cart operations
    private final IBookService bookService; // Manages book-related operations
    private final ICSVExportService csvExportService; // Handles exporting data to CSV files

    // Private constructor to initialize all services
    private ServiceManager() {
        // Initialize IBookDAO with BookDAO to manage book-related data access
        IBookDAO bookDAO = new BookDAO();

        // Initialize service instances
        this.userService = new UserService(); // Initialize user service
        this.orderService = OrderService.getInstance(); // Use singleton instance for OrderService
        this.inventoryService = new InventoryService(bookDAO); // Initialize inventory service, requires IBookDAO
        this.paymentService = PaymentService.getInstance(); // Use singleton instance for PaymentService
        this.cartService = CartService.getInstance(); // Use singleton instance for CartService
        this.bookService = BookService.getInstance(); // Use singleton instance for BookService
        this.csvExportService = CSVExportService.getInstance(); // Use singleton instance for CSVExportService
    }

    // Get singleton instance of ServiceManager
    public static synchronized ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager(); // Initialization of the ServiceManager
        }
        return instance;
    }

    // Getter methods to access services

    // Returns the instance of IUserService for user-related operations
    public IUserService getUserService() {
        return userService;
    }

    // Returns the instance of IOrderService for order-related operations
    public IOrderService getOrderService() {
        return orderService;
    }

    // Returns the instance of IInventoryService for managing inventory operations
    public IInventoryService getInventoryService() {
        return inventoryService;
    }

    // Returns the instance of IPaymentService for handling payments
    public IPaymentService getPaymentService() {
        return paymentService;
    }

    // Returns the instance of ICartService for managing shopping cart operations
    public ICartService getCartService() {
        return cartService;
    }

    // Returns the instance of IBookService for book-related operations
    public IBookService getBookService() {
        return bookService;
    }

    // Returns the instance of ICSVExportService for exporting data to CSV
    public ICSVExportService getCSVExportService() {
        return csvExportService;
    }
}
