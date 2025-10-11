package com.thereadingroom.controller.admin;

import com.thereadingroom.controller.user.EditProfileController;
import com.thereadingroom.controller.common.LoginController;
import com.thereadingroom.service.ServiceManager;
import com.thereadingroom.utils.auth.SessionManager;
import com.thereadingroom.utils.ui.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for handling the Admin sidebar functionality.
 * Provides navigation to various admin features like managing stocks, orders, users, editing profile, and logout.
 */
public class AdminSidebarController {

    @FXML
    private Label usernameLabel;  // Label to display the admin username

    @FXML
    private VBox contentArea;  // Content area for the sidebar

    // ServiceManager instance for managing services throughout the application
    private final ServiceManager serviceManager = ServiceManager.getInstance();

    /**
     * Initializes the sidebar by fetching and displaying the admin's username.
     */
    @FXML
    public void initialize() {
        // Fetch the logged-in admin's username from the session and display it
        String username = SessionManager.getInstance().getUsername();
        if (username != null) {
            usernameLabel.setText("Welcome, Admin " + username);
        } else {
            UIUtils.showError("Session Error", "Unable to fetch admin username.");
        }
    }

    /**
     * Helper method to get the current stage (window) of the application.
     *
     * @return The current stage.
     */
    private Stage getCurrentStage() {
        return (Stage) contentArea.getScene().getWindow();
    }

    /**
     * Loads a new scene without passing any user data.
     *
     * @param fxmlPath Path to the FXML file.
     * @param title    Title of the new scene.
     */
    private void loadSceneWithoutData(String fxmlPath, String title) {
        UIUtils.loadScene(fxmlPath, getCurrentStage(), title);
    }

    /**
     * Handles the "View Stocks" button click event.
     * Loads the Manage Stocks view and passes the admin's first and last name.
     */
    @FXML
    public void handleViewStocks() {
        loadSceneWithoutData("/com/thereadingroom/fxml/admin/admin_view_stocks.fxml", "Manage Stocks");
    }

    /**
     * Handles the "Manage Orders" button click event.
     * Loads the Manage Orders view.
     */
    @FXML
    public void handleViewManageOrders() {
        loadSceneWithoutData("/com/thereadingroom/fxml/admin/admin_manage_orders.fxml", "Manage All Orders");
    }

    /**
     * Handles the "Manage Users" button click event.
     * Loads the Manage Users view.
     */
    @FXML
    public void handleViewManageUsers() {
        loadSceneWithoutData("/com/thereadingroom/fxml/admin/admin_manage_users.fxml", "Manage Users");
    }

    /**
     * Handles the "Edit Profile" button click event.
     * Loads the Edit Profile view and passes the UserService to the controller.
     */
    @FXML
    public void handleEditProfile() {
        UIUtils.loadSceneWithData("/com/thereadingroom/fxml/admin/admin_edit_profile.fxml", getCurrentStage(), "Edit Profile", controller -> {
            EditProfileController profileController = (EditProfileController) controller;
            profileController.setUserService(serviceManager.getUserService());
        });
    }

    /**
     * Handles the "Logout" button click event.
     * Clears the current session and loads the login view.
     */
    @FXML
    public void handleLogout() {
        // Clear the session when logging out
        SessionManager.getInstance().clearSession();
        UIUtils.loadSceneWithData("/com/thereadingroom/fxml/common/login.fxml", getCurrentStage(), "Login", controller -> {
            LoginController loginController = (LoginController) controller;
            loginController.setUserService(serviceManager.getUserService());
        });
    }
}
