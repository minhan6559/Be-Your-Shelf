package com.beyourshelf.controller.common;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

import com.beyourshelf.controller.user.UserDashboardController;
import com.beyourshelf.model.entity.Book;
import com.beyourshelf.model.entity.ShoppingCart;
import com.beyourshelf.model.entity.User;
import com.beyourshelf.service.ServiceManager;
import com.beyourshelf.service.book.IBookService;
import com.beyourshelf.service.cart.ICartService;
import com.beyourshelf.service.user.IUserService;
import com.beyourshelf.utils.auth.SessionManager;
import com.beyourshelf.utils.ui.UIUtils;

/**
 * Controller for handling user login functionality.
 * Manages user authentication and redirects users to appropriate dashboards.
 */
public class LoginController {

    @FXML
    private TextField usernameField; // Input field for the username

    @FXML
    private PasswordField passwordField; // Input field for the password

    private IUserService userService; // Service for user-related operations

    @FXML
    private ImageView imageView; // ImageView for displaying the library image

    /**
     * Injects the user service dependency.
     *
     * @param userService The user service used for login and user validation.
     */
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        // Use helper method in UIUtils to load and set the image
        UIUtils.loadImage(imageView, "/com/beyourshelf/assets/images/Library.jpg");
    }

    /**
     * Handles the login button action.
     * Validates the input fields, checks user credentials, and redirects users
     * based on their roles.
     */
    @FXML
    public void handleLogin() {
        if (!isUserServiceInitialized())
            return;

        // Retrieve and trim user input
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input fields
        if (!validateInputFields(username, password))
            return;

        // Check if the credentials are valid and proceed accordingly
        if (userService.validateUserLogin(username, password)) {
            processSuccessfulLogin(username); // Handle login success
        } else {
            UIUtils.showError("Login Failed", "Invalid username or password.");
        }
    }

    /**
     * Checks if the user service is initialized.
     *
     * @return true if the user service is initialized, false otherwise.
     */
    private boolean isUserServiceInitialized() {
        if (userService == null) {
            throw new IllegalStateException("UserService not initialized!"); // Ensure userService is set
        }
        return true;
    }

    /**
     * Validates that the username and password fields are not empty.
     *
     * @param username The entered username.
     * @param password The entered password.
     * @return true if both fields are non-empty, false otherwise.
     */
    private boolean validateInputFields(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            UIUtils.showError("Login Failed", "Please enter both username and password.");
            return false;
        }
        return true;
    }

    /**
     * Processes successful login by setting session data and redirecting the user
     * based on their role.
     *
     * @param username The username of the successfully authenticated user.
     */
    private void processSuccessfulLogin(String username) {
        // Retrieve user details based on username
        Optional<User> userOptional = userService.getUserByUsername(username);
        userOptional.ifPresent(user -> {
            setSessionData(user); // Set session details

            // Redirect to appropriate dashboard based on user role (admin or regular user)
            if (userService.isAdminUser(username)) {
                redirectToAdminDashboard(user);
            } else {
                initializeUserDashboard(user);
            }
        });
    }

    /**
     * Sets user details in the session after successful login.
     *
     * @param user The logged-in user.
     */
    private void setSessionData(User user) {
        SessionManager.getInstance().setUserDetails(user.getId(), user.getUsername(), user.getFirstName(),
                user.getLastName());
    }

    /**
     * Redirects an admin user to the admin dashboard.
     *
     * @param user The logged-in admin user.
     */
    private void redirectToAdminDashboard(User user) {
        UIUtils.loadSceneWithData("/com/beyourshelf/fxml/admin/admin_view_stocks.fxml", getStage(),
                "Admin Dashboard", controller -> {
                });
    }

    /**
     * Initializes the user dashboard for regular users by setting up their shopping
     * cart and loading the dashboard view.
     *
     * @param user The logged-in regular user.
     */
    private void initializeUserDashboard(User user) {
        // Create a new shopping cart for the user and set it in the session
        ShoppingCart shoppingCart = createShoppingCart(user);

        // Store the cart in the session
        SessionManager.getInstance().setShoppingCart(shoppingCart);
        UIUtils.loadSceneWithData("/com/beyourshelf/fxml/user/user_dashboard.fxml", getStage(), "User Dashboard",
                controller -> {
                    UserDashboardController userController = (UserDashboardController) controller;
                    userController.setUser(user.getFirstName(), user.getLastName()); // Pass user data to the user
                                                                                     // dashboard
                    userController.setShoppingCart(shoppingCart); // Pass shopping cart to the dashboard
                });
    }

    /**
     * Creates a shopping cart for the user by retrieving their cart items from the
     * cart service.
     *
     * @param user The logged-in user.
     * @return A ShoppingCart object populated with the user's items.
     */
    private ShoppingCart createShoppingCart(User user) {
        ICartService cartService = ServiceManager.getInstance().getCartService(); // Get cart service through
                                                                                  // ServiceManager
        IBookService bookService = ServiceManager.getInstance().getBookService(); // Get book service through
                                                                                  // ServiceManager
        int cartId = cartService.getOrCreateCart(user.getId()); // Get or create cart for the user
        ShoppingCart shoppingCart = new ShoppingCart(user.getId(), cartId); // Pass both user ID and cart ID
        // Populate the shopping cart with items
        cartService.getCartItems(cartId).forEach(item -> {
            Book book = bookService.findBookById(item.getBookId());
            shoppingCart.addBook(book, item.getQuantity());
        });
        return shoppingCart;
    }

    /**
     * Handles the registration button click event, loading the registration screen.
     */
    @FXML
    public void handleRegister() {
        UIUtils.loadSceneWithData("/com/beyourshelf/fxml/common/register.fxml", getStage(), "User Registration",
                controller -> {
                    RegisterController registerController = (RegisterController) controller;
                    registerController.setUserService(ServiceManager.getInstance().getUserService()); // Inject user
                                                                                                      // service into
                                                                                                      // registration
                                                                                                      // controller
                });
    }

    /**
     * Retrieves the current stage of the login window.
     *
     * @return The current stage object.
     */
    private Stage getStage() {
        return (Stage) usernameField.getScene().getWindow();
    }
}
