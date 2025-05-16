package com.example.flowspace_projectfolder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CalenderView extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Flowspace");
        stage.setMaximized(true);
        stage.getIcons().add(new Image("file:resources/icon.png"));
        stage.centerOnScreen();

        BorderPane root = new BorderPane();

        HBox topBar = new HBox();
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button darkModeBtn = new Button("Darkmode");
        Button settingsBtn = new Button("Settings");
        Button profileBtn = new Button("Profile");

        topBar.getChildren().addAll(searchField, searchButton, spacer, darkModeBtn, settingsBtn, profileBtn);
        root.setTop(topBar);

        VBox calendarBox = new VBox();
        Label calendarLabel = new Label("Calendar");

        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {
                Region dayCell = new Region();
                dayCell.setPrefSize(100, 100);
                dayCell.setStyle("-fx-background-color: #d0ddff;");
                calendarGrid.add(dayCell, col, row);
            }
        }

        calendarBox.getChildren().addAll(calendarLabel, calendarGrid);
        root.setCenter(calendarBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
