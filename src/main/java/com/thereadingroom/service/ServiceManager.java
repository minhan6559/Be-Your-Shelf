package com.thereadingroom.service;

import com.thereadingroom.model.dao.book.BookDAO;
import com.thereadingroom.model.dao.book.IBookDAO;
import com.thereadingroom.service.CSVExport.CSVExportService;
import com.thereadingroom.service.CSVExport.ICSVExportService;
import com.thereadingroom.service.book.BookService;
import com.thereadingroom.service.book.IBookService;
import com.thereadingroom.service.cart.CartService;
import com.thereadingroom.service.cart.ICartService;
import com.thereadingroom.service.inventory.IInventoryService;
import com.thereadingroom.service.inventory.InventoryService;
import com.thereadingroom.service.order.IOrderService;
import com.thereadingroom.service.order.OrderService;
import com.thereadingroom.service.payment.IPaymentService;
import com.thereadingroom.service.payment.PaymentService;
import com.thereadingroom.service.user.IUserService;
import com.thereadingroom.service.user.UserService;

public class ServiceManager {

    // Singleton instance of ServiceManager
    private static ServiceManager instance;

    // Services managed by ServiceManager
    private final IUserService userService;                 // Handles user-related operations
    private final IOrderService orderService;               // Handles order-related operations
    private final IInventoryService inventoryService;       // Handles inventory-related operations
    private final IPaymentService paymentService;           // Handles payment processing
    private final ICartService cartService;                 // Manages shopping cart operations
    private final IBookService bookService;                 // Manages book-related operations
    private final ICSVExportService csvExportService;       // Handles exporting data to CSV files

    // Private constructor to initialize all services
    private ServiceManager() {
        // Initialize IBookDAO with BookDAO to manage book-related data access
        IBookDAO bookDAO = new BookDAO();

        // Initialize service instances
        this.userService = new UserService();               // Initialize user service
        this.orderService = OrderService.getInstance();     // Use singleton instance for OrderService
        this.inventoryService = new InventoryService(bookDAO); // Initialize inventory service, requires IBookDAO
        this.paymentService = PaymentService.getInstance(); // Use singleton instance for PaymentService
        this.cartService = CartService.getInstance();       // Use singleton instance for CartService
        this.bookService = BookService.getInstance();       // Use singleton instance for BookService
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
