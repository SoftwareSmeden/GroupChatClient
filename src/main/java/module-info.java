module com.example.groupchat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    exports com.example.groupchat.controller;
    opens com.example.groupchat.controller to javafx.fxml;
    exports com.example.groupchat.entity;
    opens com.example.groupchat.entity to javafx.fxml;
}