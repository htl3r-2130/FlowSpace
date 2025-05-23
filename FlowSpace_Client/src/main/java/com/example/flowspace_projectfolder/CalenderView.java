package com.example.flowspace_projectfolder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

        //calendar alignments
        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);
        calendarGrid.setAlignment(Pos.CENTER);

    //calendar and functionality
        //length of currentMonth
        int curMonthLength = LocalDate.now().lengthOfMonth();
        //first bracket (monday of this week)
        int firstMonday = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .getDayOfMonth();
        int dayOffset = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 7; col++) {
                //day logic for calendar
                int day = firstMonday + dayOffset;
                if (day > curMonthLength) {
                    day = day - curMonthLength;
                }
                //create region for day
                Region dayCell = new Region();
                dayCell.setPrefSize(100, 100);
                dayCell.setStyle("-fx-background-color: #d0ddff;");
                //create date-label for day
                Label dayLabel = new Label(String.valueOf(day));
                dayLabel.setTextFill(javafx.scene.paint.Color.GRAY);
                StackPane.setAlignment(dayLabel, Pos.TOP_RIGHT);
                dayLabel.setPadding(new Insets(5));
                //check if day is today & stack
                boolean isToday = (day == LocalDate.now().getDayOfMonth());
                CalendarDayCell cell = new CalendarDayCell(day, isToday);
                calendarGrid.add(cell, col, row);
                dayOffset++;
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
