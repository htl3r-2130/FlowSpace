package com.example.flowspace_projectfolder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HomeView extends Application {

    Map<LocalDate, CalendarDayCell> dateCellMap = new HashMap<>();
    public static final String[] months = new String[]{"",
            "Jänner", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember"
    };

    @Override
    public void start(Stage stage) {
        stage.setTitle("Flowspace");
        stage.setMaximized(true);
        stage.getIcons().add(new Image("file:resources/icon.png"));
        stage.centerOnScreen();

        StackPane root = new StackPane();

        BorderPane mainPane = new BorderPane();
        root.getChildren().add(mainPane);

        // Top bar
        HBox topBar = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Image searchButtonImage = new Image("file:resources/search.png");
        ImageView searchButtonImageView = new ImageView(searchButtonImage);
        searchButtonImageView.setFitWidth(32);
        searchButtonImageView.setFitHeight(32);
        searchButtonImageView.setPreserveRatio(true);
        Button searchButton = new Button();
        searchButton.setGraphic(searchButtonImageView);

        Image settingsButtonImage = new Image("file:resources/settings.png");
        ImageView settingsButtonImageView = new ImageView(settingsButtonImage);
        settingsButtonImageView.setFitWidth(32);
        settingsButtonImageView.setFitHeight(32);
        settingsButtonImageView.setPreserveRatio(true);
        Button settingsBtn = new Button();
        settingsBtn.setGraphic(settingsButtonImageView);

        Image accountButtonImage = new Image("file:resources/user.png");
        ImageView accountImageView = new ImageView(accountButtonImage);
        accountImageView.setFitWidth(32);
        accountImageView.setFitHeight(32);
        accountImageView.setPreserveRatio(true);
        Button accountBtn = new Button();
        accountBtn.setGraphic(accountImageView);

        searchButton.setStyle(
                "-fx-background-color: #d0ddff;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 50;"
        );
        settingsBtn.setStyle(
                "-fx-background-color: #d0ddff;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 50;"
        );
        accountBtn.setStyle(
                "-fx-background-color: #d0ddff;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-background-radius: 50;"
        );


        accountBtn.setOnAction(e -> AccountCenter.show(stage, stage));
        settingsBtn.setOnAction(f -> Settings.show(stage, stage));

        topBar.getChildren().addAll(searchButton, spacer, settingsBtn, accountBtn);
        topBar.setPadding(new Insets(30));
        topBar.setSpacing(10);
        mainPane.setTop(topBar);

        // Kalender grid
        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(6);
        calendarGrid.setVgap(6);
        calendarGrid.setPadding(new Insets(20));
        calendarGrid.setAlignment(Pos.TOP_CENTER);

        LocalDate today = LocalDate.now();
        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = LocalDate.of(today.getYear(), 12, 31); //Grid-Länge

        int row = 0;
        int col = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int day = date.getDayOfMonth();
            int month = date.getMonthValue();
            int year = date.getYear();

            boolean isToday = date.equals(LocalDate.now());
            boolean showMonthLabel = day == 1;

            CalendarDayCell cell = new CalendarDayCell(day, month, year, isToday, stage);
            if (showMonthLabel) {
                cell.addMonthTag(months[month].substring(0, 3));
            }
            calendarGrid.add(cell, col, row);
            dateCellMap.put(date, cell);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        ScrollPane scrollPane = new ScrollPane(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        mainPane.setCenter(scrollPane);

        // Popup erzeugen
        SearchPopup popup = new SearchPopup(root);

        // Search Button öffnet Popup
        searchButton.setOnAction(e -> {
            popup.show();
        });

        // Falls du Tasks lädst (deine Logik)
        List<String> tasks = NetworkManager.loadUserTasks();
        for (String entry : tasks) {
            String[] parts = entry.split("\\|", 2);
            if (parts.length != 2) continue;
            LocalDate date = LocalDate.parse(parts[0]);
            String text = parts[1];

            CalendarDayCell cell = dateCellMap.get(date);
            if (cell != null) {
                cell.loadEntry(text);
            }
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
