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
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HomeView extends Application {
    Map<LocalDate, CalendarDayCell> dateCellMap = new HashMap<>();
    public static final String[] months = new String[]{"",
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    public void start(Stage stage) {
        stage.setTitle("Flowspace");
        stage.setMaximized(true);
        stage.getIcons().add(new Image("file:resources/icon.png"));
        stage.centerOnScreen();

        BorderPane root = new BorderPane();

        // Top bar
        HBox topBar = new HBox();
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button darkModeBtn = new Button("Darkmode");
        Button settingsBtn = new Button("Settings");
        Button profileBtn = new Button("Profile");

        profileBtn.setOnAction(e -> AccountCenter.show(stage, stage));
        settingsBtn.setOnAction(f -> Settings.show(stage, stage));

        topBar.getChildren().addAll(searchField, searchButton, spacer, darkModeBtn, settingsBtn, profileBtn);
        topBar.setPadding(new Insets(30));
        topBar.setSpacing(10);
        root.setTop(topBar);

        // Calender grid
        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(6);
        calendarGrid.setVgap(6);
        calendarGrid.setPadding(new Insets(20));
        calendarGrid.setAlignment(Pos.TOP_CENTER);

        LocalDate today = LocalDate.now();
        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = LocalDate.of(today.getYear(), 12, 31); //Adjust grid length

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

        root.setCenter(scrollPane);

        Scene scene = new Scene(root);

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

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}