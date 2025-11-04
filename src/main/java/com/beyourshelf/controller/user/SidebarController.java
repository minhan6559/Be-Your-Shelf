package com.beyourshelf.controller.user;

import com.beyourshelf.controller.common.LoginController;
import com.beyourshelf.model.entity.ShoppingCart;
import com.beyourshelf.service.ServiceManager;
import com.beyourshelf.utils.auth.SessionManager;
import com.beyourshelf.utils.ui.UIUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for managing the sidebar navigation in the user dashboard.
 * Provides navigation to various sections like books, user dashboard, orders,
 * cart, and profile management.
 */
public class SidebarController {

    @FXML
    private Label usernameLabel; // Label to display the current user's username

    @FXML
    private VBox contentArea; // Main content area to manage the current view

    /**
     * Initializes the sidebar by displaying the current user's username.
     */
    @FXML
    public void initialize() {
        displayUsername(); // Show the logged-in user's username in the sidebar
    }

    /**
     * Retrieves and displays the current user's username from the session.
     */
    private void displayUsername() {
        String username = SessionManager.getInstance().getUsername(); // Get the username from the session
        usernameLabel.setText("Welcome, " + username); // Display the username in the sidebar
    }

    /**
     * Retrieves the current stage for scene transitions.
     *
     * @return The current stage of the scene.
     */
    private Stage getCurrentStage() {
        return (Stage) contentArea.getScene().getWindow(); // Get the window associated with the current content area
    }

    /**
     * Loads a new scene and passes controller data to the corresponding controller.
     *
     * @param fxmlPath       The path to the FXML file for the scene.
     * @param title          The title of the new scene.
     * @param controllerData The data to be passed to the controller.
     */
    private void loadSceneWithController(String fxmlPath, String title, Object controllerData) {
        UIUtils.loadSceneWithData(fxmlPath, getCurrentStage(), title, controller -> {
            setupControllerData(controller, controllerData); // Set up the controller with the necessary data
        });
    }

    /**
     * Helper method to pass data to various controllers.
     * Handles different types of controllers and injects necessary data.
     *
     * @param controller     The controller object.
     * @param controllerData The data to be injected into the controller.
     */
    private void setupControllerData(Object controller, Object controllerData) {
        if (controller instanceof ShoppingCartController) {
            setupShoppingCartController((ShoppingCartController) controller, (ShoppingCart) controllerData);
        } else if (controller instanceof OrderController) {
            setupOrderController((OrderController) controller, (int) controllerData);
        } else if (controller instanceof UserDashboardController) {
            setupUserDashboardController((UserDashboardController) controller, (ShoppingCart) controllerData);
        } else if (controller instanceof ViewAllBooksController) {
            setupViewAllBooksController((ViewAllBooksController) controller, (ShoppingCart) controllerData);
        }
    }

    /**
     * Sets up the ShoppingCartController with the user's shopping cart data.
     *
     * @param controller   The ShoppingCartController instance.
     * @param shoppingCart The user's shopping cart data.
     */
    private void setupShoppingCartController(ShoppingCartController controller, ShoppingCart shoppingCart) {
        controller.setShoppingCart(shoppingCart); // Inject the shopping cart into the controller
    }

    /**
     * Sets up the OrderController with the user's order data.
     *
     * @param controller The OrderController instance.
     * @param userId     The ID of the user whose orders are being viewed.
     */
    private void setupOrderController(OrderController controller, int userId) {
        controller.setOrderService(ServiceManager.getInstance().getOrderService()); // Inject the order service
        controller.setUserId(userId); // Inject the user ID
    }

    /**
     * Sets up the UserDashboardController with the user's dashboard data.
     *
     * @param controller   The UserDashboardController instance.
     * @param shoppingCart The user's shopping cart data.
     */
    private void setupUserDashboardController(UserDashboardController controller, ShoppingCart shoppingCart) {
        controller.setUser(SessionManager.getInstance().getFirstName(), SessionManager.getInstance().getLastName()); // Set
                                                                                                                     // the
                                                                                                                     // user's
                                                                                                                     // name
        controller.setShoppingCart(shoppingCart); // Set the shopping cart in the controller
    }

    /**
     * Sets up the ViewAllBooksController with the user's shopping cart data.
     *
     * @param controller   The ViewAllBooksController instance.
     * @param shoppingCart The user's shopping cart data.
     */
    private void setupViewAllBooksController(ViewAllBooksController controller, ShoppingCart shoppingCart) {
        controller.setShoppingCart(shoppingCart); // Set the shopping cart in the controller
    }

    /**
     * Handles the event for viewing all books.
     * Loads the "All Books" scene with the user's shopping cart data.
     */
    @FXML
    public void handleViewAllBooks() {
        loadSceneWithController("/com/beyourshelf/fxml/user/all_books.fxml", "All Books",
                getShoppingCartFromSession());
    }

    /**
     * Handles the event for viewing the user dashboard.
     * Loads the user dashboard scene with the user's shopping cart data.
     */
    @FXML
    public void handleViewUserDashboard() {
        loadSceneWithController("/com/beyourshelf/fxml/user/user_dashboard.fxml", "User Dashboard",
                getShoppingCartFromSession());
    }

    /**
     * Handles the event for viewing the user's orders.
     * Loads the "View Orders" scene with the user's order data.
     */
    @FXML
    public void handleViewOrders() {
        loadSceneWithController("/com/beyourshelf/fxml/user/view_orders.fxml", "Your Orders",
                SessionManager.getInstance().getUserId());
    }

    /**
     * Handles the event for editing the user's profile.
     * Loads the edit profile scene and injects the necessary user service.
     */
    @FXML
    public void handleEditProfile() {
        UIUtils.loadSceneWithData("/com/beyourshelf/fxml/user/edit_profile.fxml", getCurrentStage(), "Edit Profile",
                controller -> {
                    EditProfileController profileController = (EditProfileController) controller;
                    profileController.setUserService(ServiceManager.getInstance().getUserService()); // Inject the user
                                                                                                     // service
                });
    }

    /**
     * Handles the user logout action.
     * Clears the current session and redirects to the login screen.
     */
    @FXML
    public void handleLogout() {
        SessionManager.getInstance().clearSession(); // Clear the session data
        UIUtils.loadSceneWithData("/com/beyourshelf/fxml/common/login.fxml", getCurrentStage(), "Login",
                controller -> {
                    LoginController loginController = (LoginController) controller;
                    loginController.setUserService(ServiceManager.getInstance().getUserService()); // Inject the user
                                                                                                   // service
                });
    }

    /**
     * Handles the event for viewing the shopping cart.
     * Loads the shopping cart scene with the user's shopping cart data.
     */
    @FXML
    public void handleViewCart() {
        loadSceneWithController("/com/beyourshelf/fxml/user/shopping_cart.fxml", "Shopping Cart",
                getShoppingCartFromSession());
    }

    /**
     * Retrieves the user's shopping cart from the session.
     *
     * @return The user's shopping cart.
     */
    private ShoppingCart getShoppingCartFromSession() {
        return SessionManager.getInstance().getShoppingCart(); // Get the shopping cart from the session
    }
}
