package com.beyourshelf.controller.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.beyourshelf.model.entity.Order;
import com.beyourshelf.service.order.IOrderService;
import com.beyourshelf.utils.auth.SessionManager;
import com.beyourshelf.utils.ui.UIUtils;

/**
 * Controller responsible for displaying and managing the user's order history.
 * Supports order export functionality and order list display.
 */
public class OrderController {

    private List<Order> userOrders; // Holds the list of the user's orders
    private IOrderService orderService; // Service for handling order-related operations
    private List<CheckBox> orderCheckBoxes = new ArrayList<>(); // Track checkboxes for each order

    @FXML
    private Button exportOrdersButton; // Button to export selected orders to CSV

    @FXML
    private ListView<HBox> orderListView; // ListView to display orders with checkboxes

    /**
     * Injects the order service dependency.
     *
     * @param orderService The service used to handle order-related operations.
     */
    public void setOrderService(IOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Initializes the controller by disabling the export button and setting up the
     * ListView.
     */
    @FXML
    public void initialize() {
        exportOrdersButton.setDisable(true); // Disable the export button until orders are loaded
        UIUtils.loadCSS(orderListView, "/com/beyourshelf/css/table-style.css");
    }

    /**
     * Loads the user's orders using the provided userId.
     *
     * @param userId The ID of the user whose orders are to be fetched.
     */
    public void setUserId(int userId) {
        if (orderService != null) {
            loadUserOrders(userId); // Fetch and display user orders
        } else {
            UIUtils.showError("Error", "OrderService is not initialized.");
        }
    }

    /**
     * Fetches and loads the user's orders into the ListView with checkboxes.
     *
     * @param userId The ID of the user whose orders are to be displayed.
     */
    private void loadUserOrders(int userId) {
        userOrders = orderService.getAllOrdersByUser(userId);
        orderListView.getItems().clear(); // Clear previous order entries
        orderCheckBoxes.clear(); // Clear previous checkboxes

        if (userOrders.isEmpty()) {
            orderListView.getItems().add(new HBox(new Label("No orders found.")));
            exportOrdersButton.setDisable(true); // Disable export button if no orders are available
        } else {
            userOrders.forEach(order -> {
                HBox orderItem = createOrderItem(order); // Create HBox with a checkbox and label
                orderListView.getItems().add(orderItem); // Add the item to the ListView
            });
            exportOrdersButton.setDisable(false); // Enable export button when there are orders
        }
    }

    /**
     * Creates an HBox representing an order with a CheckBox and a Label.
     *
     * @param order The order to display.
     * @return HBox containing the checkbox and formatted order details.
     */
    private HBox createOrderItem(Order order) {
        CheckBox checkBox = new CheckBox(); // Checkbox for selecting the order
        Label orderLabel = new Label(formatOrderForDisplay(order)); // Label for displaying order details
        orderCheckBoxes.add(checkBox); // Add checkbox to the list of checkboxes
        HBox orderBox = new HBox(10, checkBox, orderLabel); // Create an HBox with spacing
        orderBox.setStyle("-fx-padding: 5px;"); // Add padding to each item
        return orderBox;
    }

    /**
     * Formats the order details for display in the ListView.
     *
     * @param order The order to format.
     * @return A string representing the order and its items for display.
     */
    private String formatOrderForDisplay(Order order) {
        StringBuilder orderDetails = new StringBuilder(String.format("Order #%s | Date: %s | Total: $%.2f\n",
                order.getOrderNumber(), // Order number
                order.getFormattedOrderDate(), // Order date
                order.getTotalPrice())); // Total order price

        order.getOrderItems().forEach(item -> orderDetails.append(String.format("   - %s (x%d): $%.2f\n",
                item.getTitle(), // Item title
                item.getQuantity(), // Quantity of the item
                item.getPrice()))); // Item price

        return orderDetails.toString(); // Return formatted order details
    }

    /**
     * Handles the export of selected orders to a CSV file.
     * This method is triggered when the "Export" button is clicked.
     */
    @FXML
    public void handleExportOrders() {
        List<Order> selectedOrders = getSelectedOrders(); // Get the orders corresponding to the checked checkboxes

        if (selectedOrders.isEmpty()) {
            UIUtils.showAlert("No Selection", "Please select at least one order to export.");
            return;
        }

        // Prompt the user to select a file location for saving the CSV
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Orders as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showSaveDialog(exportOrdersButton.getScene().getWindow());

        if (file != null) {
            exportOrdersToFile(file, selectedOrders); // Export the selected orders to the chosen file
        }
    }

    /**
     * Retrieves the orders corresponding to the checked checkboxes.
     *
     * @return List of selected orders.
     */
    private List<Order> getSelectedOrders() {
        return userOrders.stream()
                .filter(order -> {
                    int index = userOrders.indexOf(order);
                    return orderCheckBoxes.get(index).isSelected(); // Check if the corresponding checkbox is selected
                })
                .collect(Collectors.toList());
    }

    /**
     * Exports the selected orders to a CSV file.
     *
     * @param file           The file to export the orders to.
     * @param selectedOrders The selected orders to export.
     */
    private void exportOrdersToFile(java.io.File file, List<Order> selectedOrders) {
        List<Integer> selectedOrderIds = selectedOrders.stream().map(Order::getOrderId).collect(Collectors.toList());
        boolean success = orderService.exportOrdersToCSV(SessionManager.getInstance().getUserId(), selectedOrderIds,
                file.getAbsolutePath());

        // Show a success or error message based on the export result
        if (success) {
            UIUtils.showAlert("Export Successful", "Orders exported to: " + file.getAbsolutePath());
        } else {
            UIUtils.showError("Export Failed", "Failed to export orders.");
        }
    }
}
