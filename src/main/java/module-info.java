module com.thereadingroom {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires transitive java.sql;
    requires com.zaxxer.hikari;

    // Open specific packages to javafx.fxml for reflection
    opens com.thereadingroom.controller.admin to javafx.fxml; // Opens admin controllers to FXML
    opens com.thereadingroom.controller.user to javafx.fxml, javafx.base; // Opens user controllers to FXML
    opens com.thereadingroom.view to javafx.fxml; // Opens the view package to FXML

    // Open model.dao package for reflection (needed for FXML access to DAO classes)
    opens com.thereadingroom.model.dao to javafx.fxml;

    // Export packages to make them accessible for other modules (if needed)
    exports com.thereadingroom.controller.admin to javafx.fxml; // Export admin controllers
    exports com.thereadingroom.controller.user to javafx.fxml; // Export user controllers
    exports com.thereadingroom.view; // Export the view package

    // Export model.entity and model.dao packages if needed by other modules
    exports com.thereadingroom.model.entity;
    exports com.thereadingroom.model.dao;
    exports com.thereadingroom.service.book;
    exports com.thereadingroom.service.order;
    exports com.thereadingroom.service.payment;
    exports com.thereadingroom.service.user;
    exports com.thereadingroom.service.inventory;
    exports com.thereadingroom.service.CSVExport;
    exports com.thereadingroom.service.cart;
    exports com.thereadingroom.model.dao.book;

    opens com.thereadingroom.model.dao.book to javafx.fxml;

    exports com.thereadingroom.model.dao.cart;

    opens com.thereadingroom.model.dao.cart to javafx.fxml;

    exports com.thereadingroom.model.dao.user;

    opens com.thereadingroom.model.dao.user to javafx.fxml;

    exports com.thereadingroom.model.dao.order;

    opens com.thereadingroom.model.dao.order to javafx.fxml;

    exports com.thereadingroom.model.dao.database;

    opens com.thereadingroom.model.dao.database to javafx.fxml;
    opens com.thereadingroom.model.entity to javafx.base, javafx.fxml;

    exports com.thereadingroom.controller.common to javafx.fxml;

    opens com.thereadingroom.controller.common to javafx.base, javafx.fxml;
}
