package com.beyourshelf.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.beyourshelf.controller.common.LoginController;
import com.beyourshelf.model.dao.database.DatabaseInitializer;
import com.beyourshelf.service.user.IUserService;
import com.beyourshelf.service.user.UserService;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize the database before starting the application
        try {
            DatabaseInitializer.initializeDatabase(); // Initialize tables and data
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return; // Stop application if database initialization fails
        }

        try {
            // Load the login FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beyourshelf/fxml/common/login.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 720); // Load the scene with default resolution

            // Get the controller associated with the login FXML
            LoginController loginController = loader.getController();

            // Inject the IUserService implementation into the LoginController
            IUserService userService = UserService.getInstance(); // Retrieve singleton instance of UserService
            loginController.setUserService(userService); // Inject the user service into the login controller

            // Set the scene to the stage
            primaryStage.setScene(scene); // Set the scene to be displayed on the stage
            primaryStage.setTitle("Login"); // Set the window title
            primaryStage.setResizable(false); // Prevent users from resizing the window
            primaryStage.show(); // Show the login window
        } catch (IOException e) {
            System.err.println("Error loading the login scene: " + e.getMessage()); // Handle loading error
        }
    }

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
