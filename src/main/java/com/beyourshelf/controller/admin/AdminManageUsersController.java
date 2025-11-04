package com.beyourshelf.controller.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.stream.Collectors;

import com.beyourshelf.model.entity.User;
import com.beyourshelf.service.ServiceManager;
import com.beyourshelf.service.user.IUserService;
import com.beyourshelf.utils.ui.UIUtils;

/**
 * Controller class for managing user accounts in the admin panel.
 * This allows an admin to view, search, edit, and remove users.
 */
public class AdminManageUsersController {

    @FXML
    private TableView<User> userTableView; // Table view for displaying users

    @FXML
    private TableColumn<User, Integer> userIdColumn; // Column for displaying user ID

    @FXML
    private TableColumn<User, String> usernameColumn; // Column for displaying username

    @FXML
    private TableColumn<User, String> firstNameColumn; // Column for displaying user's first name

    @FXML
    private TableColumn<User, String> lastNameColumn; // Column for displaying user's last name

    @FXML
    private TableColumn<User, Void> actionColumn; // Column for displaying action buttons (Edit, Remove)

    @FXML
    private TextField searchField; // Input field for searching users

    // Service for handling user-related operations
    private final IUserService userService = ServiceManager.getInstance().getUserService();

    // List to hold all user data loaded from the database
    private List<User> allUsers;

    /**
     * Initializes the controller by setting up the table columns, loading the
     * users, and applying the CSS.
     */
    @FXML
    public void initialize() {
        setupTableColumns(); // Setup table column mappings
        addActionButtonsToTable(); // Add action buttons to the table
        loadUsers(); // Load users into the table on initialization
        applyStyles(); // Apply custom CSS styles
    }

    /**
     * Applies the CSS stylesheet using UIUtils.
     */
    private void applyStyles() {
        UIUtils.loadCSS(userTableView, "/com/beyourshelf/css/table-style.css"); // Adjust path if necessary
    }

    /**
     * Configures the table columns to map the user attributes to the correct fields
     * in the table.
     */
    private void setupTableColumns() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    }

    /**
     * Adds Edit and Remove buttons to each row in the action column of the table.
     */
    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = createEditButton();
                    private final Button removeButton = createRemoveButton();

                    private Button createEditButton() {
                        Button button = new Button("Edit");
                        button.setStyle(
                                "-fx-background-color: #228b22; -fx-text-fill: white; -fx-border-radius: 15; -fx-padding: 8 15;");
                        button.setOnAction(_ -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);
                        });
                        return button;
                    }

                    private Button createRemoveButton() {
                        Button button = new Button("Remove");
                        button.setStyle(
                                "-fx-background-color: #d2691e; -fx-text-fill: white; -fx-border-radius: 15; -fx-padding: 8 15;");
                        button.setOnAction(_ -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleRemoveUser(user);
                        });
                        return button;
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox actionButtons = new HBox(editButton, removeButton);
                            actionButtons.setSpacing(10); // Set spacing between the buttons
                            actionButtons.setAlignment(Pos.CENTER); // Align buttons in the center
                            setGraphic(actionButtons);
                        }
                    }
                };
            }
        });
    }

    /**
     * Fetches the user data from the database and loads it into the table view.
     * Filters out admin users.
     */
    private void loadUsers() {
        allUsers = userService.getAllUsers()
                .stream()
                .filter(user -> !user.isAdmin()) // Filter out admin users from the list
                .collect(Collectors.toList());

        updateTableView(allUsers); // Update the table view with the user list
    }

    /**
     * Updates the table view with the provided list of users.
     *
     * @param users The list of users to display in the table.
     */
    private void updateTableView(List<User> users) {
        userTableView.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * Handles the search operation by filtering users based on the search query.
     * If the search field is empty, all users are displayed again.
     */
    @FXML
    public void handleSearchUser() {
        String query = searchField.getText().trim().toLowerCase();

        // If no search query is provided, show all users
        if (query.isEmpty()) {
            updateTableView(allUsers);
            return;
        }

        // Filter users based on the query
        List<User> filteredUsers = filterUsers(query);
        if (filteredUsers.isEmpty()) {
            UIUtils.showError("Search Error", "No matching users found.");
        } else {
            updateTableView(filteredUsers); // Show filtered results
        }
    }

    /**
     * Filters users by their ID or username.
     *
     * @param query The search query entered by the admin.
     * @return A list of users matching the query.
     */
    private List<User> filterUsers(String query) {
        return allUsers.stream()
                .filter(user -> String.valueOf(user.getId()).equals(query) ||
                        user.getUsername().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    /**
     * Handles the editing of the selected user.
     * Opens the edit user form in a modal dialog.
     *
     * @param user The selected user to edit.
     */
    private void handleEditUser(User user) {
        UIUtils.loadModal("/com/beyourshelf/fxml/admin/admin_edit_user.fxml", "Edit User", controller -> {
            AdminEditUserController editController = (AdminEditUserController) controller;
            editController.setUser(user); // Pass the selected user to the edit controller
        }, (Stage) userTableView.getScene().getWindow());
    }

    /**
     * Handles the removal of the selected user.
     * Prompts the admin to confirm the deletion before proceeding.
     *
     * @param user The selected user to remove.
     */
    private void handleRemoveUser(User user) {
        if (confirmUserDeletion()) {
            deleteUser(user); // Proceed with user deletion if confirmed
        }
    }

    /**
     * Prompts the admin to confirm the deletion of a user.
     *
     * @return true if the admin confirms, false otherwise.
     */
    private boolean confirmUserDeletion() {
        return UIUtils.showConfirmation("Confirm Deletion", "Are you sure you want to delete the user?");
    }

    /**
     * Deletes the selected user from the system.
     *
     * @param user The user to be deleted.
     */
    private void deleteUser(User user) {
        boolean success = userService.deleteUser(user.getId());
        if (success) {
            UIUtils.showAlert("Success", "User removed successfully.");
            loadUsers(); // Reload the updated list of users after deletion
        } else {
            UIUtils.showError("Error", "Failed to remove user.");
        }
    }
}
