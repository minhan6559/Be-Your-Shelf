package com.beyourshelf.utils.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

public class UIUtils {

    // Default scene size constants for convenience
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;

    /**
     * Displays an informational alert dialog to the user.
     *
     * @param title   The title of the alert dialog.
     * @param message The message to display in the alert.
     */
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header for simplicity
        alert.setContentText(message);
        alert.showAndWait(); // Blocks until user closes the dialog
    }

    /**
     * Displays an error alert dialog to the user.
     *
     * @param title   The title of the error dialog.
     * @param message The error message to display.
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header for simplicity
        alert.setContentText(message);
        alert.showAndWait(); // Blocks until user closes the dialog
    }

    /**
     * Displays a confirmation dialog with OK and Cancel buttons.
     *
     * @param title   The title of the confirmation dialog.
     * @param message The message to display.
     * @return true if OK is pressed, false otherwise.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header for simplicity
        alert.setContentText(message);

        // Capture the user's response (OK/Cancel)
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Loads a scene from an FXML file and displays it in the specified stage.
     * Uses default dimensions for the scene (1280x720).
     *
     * @param fxmlPath The path to the FXML file.
     * @param stage    The stage where the scene will be loaded.
     * @param title    The title of the stage (window).
     */
    public static void loadScene(String fxmlPath, Stage stage, String title) {
        loadScene(fxmlPath, stage, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Loads a scene from an FXML file and displays it in the specified stage.
     * The dimensions of the scene are customizable.
     *
     * @param fxmlPath The path to the FXML file.
     * @param stage    The stage where the scene will be loaded.
     * @param title    The title of the stage (window).
     * @param width    The width of the scene.
     * @param height   The height of the scene.
     */
    public static void loadScene(String fxmlPath, Stage stage, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource(fxmlPath)); // Load FXML
            Parent view = loader.load(); // Load the scene root
            Scene scene = new Scene(view, width, height); // Create a scene with specific dimensions
            stage.setScene(scene); // Set the scene on the stage
            stage.setTitle(title); // Set the title of the window
            stage.setResizable(false); // Disable window resizing
            stage.show(); // Show the window
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for debugging
            showError("Error", "Unable to load the view: " + e.getMessage()); // Show error dialog
        }
    }

    /**
     * Loads a modal window (blocks interaction with other windows) using the
     * specified FXML file.
     * The modal blocks interaction with the owner window until closed.
     *
     * @param fxmlPath   The path to the FXML file.
     * @param title      The title of the modal window.
     * @param dataLoader A callback interface to pass data to the controller.
     * @param owner      The owner stage of the modal.
     * @param <T>        The type of controller used in the modal window.
     */
    public static <T> void loadModal(String fxmlPath, String title, DataLoader<T> dataLoader, Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource(fxmlPath)); // Load FXML
            Parent root = loader.load(); // Load the scene root
            dataLoader.loadData(loader.getController()); // Pass data to the controller

            // Create and configure the modal stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
            modalStage.initOwner(owner); // Set the owner window
            modalStage.setScene(new Scene(root)); // Set the scene with default size
            modalStage.setTitle(title); // Set the title of the window
            modalStage.setResizable(false); // Disable window resizing
            modalStage.showAndWait(); // Show the modal and wait for it to be closed
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for debugging
            showError("Error", "Unable to load the modal: " + e.getMessage()); // Show error dialog
        }
    }

    /**
     * Loads a scene from an FXML file and passes data to the controller.
     *
     * @param fxmlPath   The path to the FXML file.
     * @param stage      The stage where the scene will be loaded.
     * @param title      The title of the stage (window).
     * @param dataLoader A callback interface to pass data to the controller.
     * @param <T>        The type of controller.
     */
    public static <T> void loadSceneWithData(String fxmlPath, Stage stage, String title, DataLoader<T> dataLoader) {
        try {
            FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource(fxmlPath)); // Load FXML
            Parent view = loader.load(); // Load the scene root
            dataLoader.loadData(loader.getController()); // Pass data to the controller
            Scene scene = new Scene(view, DEFAULT_WIDTH, DEFAULT_HEIGHT); // Create a scene with default size
            stage.setScene(scene); // Set the scene on the stage
            stage.setTitle(title); // Set the title of the window
            stage.setResizable(false); // Disable window resizing
            stage.show(); // Display the window
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for debugging
            showError("Error", "Unable to load the view: " + e.getMessage()); // Show error dialog
        }
    }

    /**
     * Interface to pass data to controllers during FXML loading.
     *
     * @param <T> The type of the controller.
     */
    public interface DataLoader<T> {
        void loadData(T controller); // Method to pass data to the controller
    }

    /**
     * Closes the current window from any UI node.
     *
     * @param node The node inside the window to be closed.
     */
    public static void closeCurrentWindow(Node node) {
        Stage stage = (Stage) node.getScene().getWindow(); // Get the current window (stage)
        stage.close(); // Close the window
    }

    /**
     * Loads an image into an ImageView with error handling.
     *
     * @param imageView The ImageView where the image will be loaded.
     * @param imagePath The path to the image resource.
     */
    public static void loadImage(ImageView imageView, String imagePath) {
        InputStream imageStream = UIUtils.class.getResourceAsStream(imagePath);
        if (imageStream == null) {
            showError("Image Load Error", "Unable to load image from path: " + imagePath);
            return;
        }
        try {
            Image image = new Image(imageStream); // Load the image from the path
            imageView.setImage(image); // Set the image in the ImageView
        } catch (Exception e) {
            showError("Image Load Error", "Error loading image: " + imagePath);
            e.printStackTrace();
        }
    }

    /**
     * Loads a CSS stylesheet into the specified root node.
     * Handles potential null values from the resource lookup.
     *
     * @param root    The root node (scene or parent) where the stylesheet will be
     *                applied.
     * @param cssPath The path to the CSS stylesheet.
     */
    public static void loadCSS(Parent root, String cssPath) {
        // Attempt to get the resource
        URL resource = UIUtils.class.getResource(cssPath);

        // Check if the resource is not null before calling toExternalForm
        if (resource != null) {
            // Safely apply the stylesheet if the resource exists
            String stylesheetPath = resource.toExternalForm();
            root.getStylesheets().add(stylesheetPath);
        } else {
            // Handle cases where the resource could not be found
            System.err.println("Error: Unable to load CSS from path: " + cssPath);
        }
    }
}
