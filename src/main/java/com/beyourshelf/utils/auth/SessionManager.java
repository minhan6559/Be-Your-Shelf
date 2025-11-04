package com.beyourshelf.utils.auth;

import com.beyourshelf.model.entity.ShoppingCart;

public class SessionManager {

    // Singleton instance of SessionManager
    private static SessionManager instance;

    // Fields to store user information and shopping cart for the session
    private int userId;
    private String username; // Stores the username of the logged-in user
    private String firstName;
    private String lastName;
    private ShoppingCart shoppingCart; // Stores the shopping cart for the session

    // Private constructor to ensure only one instance (Singleton pattern)
    private SessionManager() {
    }

    /**
     * Get the singleton instance of SessionManager.
     * Ensures only one instance is created.
     *
     * @return The singleton instance of SessionManager.
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager(); // Create the instance if it doesn't exist
        }
        return instance;
    }

    /**
     * Set user details after login.
     * This method sets the user information for the session.
     *
     * @param userId    The ID of the logged-in user.
     * @param username  The username of the logged-in user.
     * @param firstName The first name of the logged-in user.
     * @param lastName  The last name of the logged-in user.
     */
    public void setUserDetails(int userId, String username, String firstName, String lastName) {
        this.userId = userId;
        this.username = username; // Store the username
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getter methods to retrieve session data

    /**
     * Get the user ID of the logged-in user.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Get the username of the logged-in user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username; // Return the stored username
    }

    /**
     * Get the first name of the logged-in user.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get the last name of the logged-in user.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the shopping cart for the current session.
     *
     * @param shoppingCart The ShoppingCart object.
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    /**
     * Get the shopping cart for the current session.
     *
     * @return The ShoppingCart object.
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Clear the session data.
     * This method clears all session data, typically used during logout.
     */
    public void clearSession() {
        this.userId = 0;
        this.username = null; // Clear the stored username
        this.firstName = null;
        this.lastName = null;
        this.shoppingCart = null; // Clear the shopping cart
    }
}
