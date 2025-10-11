package com.thereadingroom.controller.admin;

import com.thereadingroom.model.entity.Order;
import com.thereadingroom.service.order.IOrderService;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for managing orders in the admin panel.
 * Allows the admin to view, filter, sort, export, and remove orders.
 */
public class AdminOrderController {

    @FXML
    private TableView<Order> orderTableView;  // Table view for displaying orders

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;  // Column for order ID

    @FXML
    private TableColumn<Order, Integer> userIdColumn;  // Column for user ID

    @FXML
    private TableColumn<Order, String> orderNumberColumn;  // Column for order number

    @FXML
    private TableColumn<Order, Double> orderTotalColumn;  // Column for order total price

    @FXML
    private TableColumn<Order, String> orderDateColumn;  // Column for order date

    @FXML
    private TableColumn<Order, Void> actionColumn;  // Column for Remove button action

    @FXML
    private TextField filterUserIdField;  // Input field for filtering orders by user ID

    @FXML
    private ComboBox<String> sortComboBox;  // Combo box for sorting options

    @FXML
    private Button exportOrdersButton;  // Button for exporting orders to CSV

    // Injected service for handling order-related operations
    private final IOrderService orderService = ServiceManager.getInstance().getOrderService();

    // List to store all fetched orders
    private List<Order> allOrders;

    /**
     * Initializes the controller by setting up table columns, action buttons, and loading orders.
     */
    @FXML
    public void initialize() {
        setupTableColumns();  // Initialize table column mappings
        setupActionButtons();  // Add action buttons (Remove) to the table
        setupSortComboBox();  // Setup sorting options in the ComboBox
        loadOrders();  // Load all orders during initialization
        UIUtils.loadCSS(orderTableView, "/com/thereadingroom/css/table-style.css");
    }

    /**
     * Sets up the table columns to map the Order attributes to the table view.
     */
    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedOrderDate"));

        // Enable multiple selection for orders in the table
        orderTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Adds Remove buttons to the action column for each order in the table.
     */
    private void setupActionButtons() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
                return new TableCell<>() {
                    private final Button removeButton = new Button("Remove");

                    {
                        // Attach event handler to the Remove button
                        removeButton.setOnAction(_ -> {
                            Order order = getTableView().getItems().get(getIndex());
                            handleRemoveOrder(order);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);  // Hide button if the cell is empty
                        } else {
                            setGraphic(removeButton);  // Show button if cell has content
                        }
                    }
                };
            }
        });
    }

    /**
     * Sets up sorting options for the ComboBox.
     * Options include sorting by "Order Date" and "Total Price".
     */
    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList("Order Date", "Total Price"));
    }

    /**
     * Loads all orders from the order service and displays them in the table view.
     */
    private void loadOrders() {
        allOrders = orderService.getAllOrders();
        updateTableView(allOrders);  // Display the fetched orders in the table
    }

    /**
     * Applies filter and sorting to the list of orders based on user input.
     */
    @FXML
    public void applyFilterAndSort() {
        List<Order> filteredOrders = applyFilter(allOrders, filterUserIdField.getText().trim());  // Apply user ID filter
        List<Order> sortedOrders = applySort(filteredOrders, sortComboBox.getValue());  // Apply selected sort option
        updateTableView(sortedOrders);  // Update the table view with the filtered and sorted orders
    }

    /**
     * Filters orders based on the user ID provided in the input field.
     *
     * @param orders       The list of orders to filter.
     * @param userIdFilter The user ID filter input.
     * @return A filtered list of orders that match the user ID.
     */
    private List<Order> applyFilter(List<Order> orders, String userIdFilter) {
        if (userIdFilter.isEmpty()) {
            return orders;  // If no filter is provided, return the original list
        }
        return orders.stream()
                .filter(order -> String.valueOf(order.getUserId()).equals(userIdFilter))
                .collect(Collectors.toList());
    }

    /**
     * Sorts the filtered list of orders based on the selected sort option.
     *
     * @param orders    The list of orders to sort.
     * @param sortOption The selected sorting criterion (Order Date or Total Price).
     * @return A sorted list of orders.
     */
    private List<Order> applySort(List<Order> orders, String sortOption) {
        if ("Order Date".equals(sortOption)) {
            orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
        } else if ("Total Price".equals(sortOption)) {
            orders.sort((o1, o2) -> Double.compare(o2.getTotalPrice(), o1.getTotalPrice()));
        }
        return orders;
    }

    /**
     * Updates the table view with the provided list of orders.
     *
     * @param orders The list of orders to display.
     */
    private void updateTableView(List<Order> orders) {
        orderTableView.setItems(FXCollections.observableArrayList(orders));
    }

    /**
     * Handles the export of selected orders to a CSV file.
     * Prompts the admin to select orders and specify the file location for saving.
     */
    @FXML
    public void handleExportOrders() {
        List<Order> selectedOrders = orderTableView.getSelectionModel().getSelectedItems();

        if (selectedOrders.isEmpty()) {
            UIUtils.showError("No Selection", "Please select at least one order to export.");
            return;
        }

        // Open a file chooser dialog to select the save location for the CSV
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Orders as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(exportOrdersButton.getScene().getWindow());

        if (file != null) {
            // Call the admin-specific export method
            exportSelectedOrdersToAdminCSV(selectedOrders, file);
        }
    }

    /**
     * Exports the selected orders to a CSV file, including admin-specific details.
     *
     * @param selectedOrders The list of selected orders to export.
     * @param file           The file to which the orders will be saved.
     */
    private void exportSelectedOrdersToAdminCSV(List<Order> selectedOrders, File file) {
        List<Integer> selectedOrderIds = selectedOrders.stream().map(Order::getOrderId).collect(Collectors.toList());
        boolean success = orderService.adminExportOrdersToCSV(selectedOrderIds, file.getAbsolutePath());

        if (success) {
            UIUtils.showAlert("Export Successful", "Orders exported successfully to: " + file.getAbsolutePath());
        } else {
            UIUtils.showError("Export Failed", "Failed to export selected orders.");
        }
    }

    /**
     * Handles the removal of a specific order from the system.
     *
     * @param order The order to be removed.
     */
    private void handleRemoveOrder(Order order) {
        boolean confirm = UIUtils.showConfirmation("Confirm Deletion", "Are you sure you want to remove this order?");
        if (confirm) {
            // Ensure the order ID is valid before attempting to delete
            if (order.getOrderId() == 0) {
                UIUtils.showError("Error", "Order ID is invalid. Cannot remove this order.");
                return;
            }
            boolean success = orderService.deleteOrderById(order.getOrderId());
            if (success) {
                UIUtils.showAlert("Success", "Order removed successfully!");
                loadOrders();  // Reload the table after successful deletion
            } else {
                UIUtils.showError("Error", "Failed to remove order. Order may not exist or there was an issue.");
            }
        }
    }


}
