module com.example.flowspace_projectfolder {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.flowspace_projectfolder to javafx.fxml;
    exports com.example.flowspace_projectfolder;
}