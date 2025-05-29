package com.example.flowspace_projectfolder;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static com.example.flowspace_projectfolder.HomeView.months;

public class CalendarDayCell extends StackPane {
    private final List<String> entries = new ArrayList<>();
    private final VBox entryBox = new VBox();
    private final int day;
    private final int month;
    private final int year;

    public CalendarDayCell(int day,int month, int year, boolean isToday) {
        this.day = day;
        this.month = month;
        this.year = year;

        Region background = new Region();
        background.setPrefSize(200, 200);
        background.setStyle("-fx-background-color: #d0ddff;");

        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setPadding(new Insets(10));
        StackPane.setAlignment(dayLabel, Pos.TOP_RIGHT);
        entryBox.setSpacing(2);
        entryBox.setPadding(new Insets(35, 5, 5, 5));

        if (isToday) {
            Circle redCircle = new Circle(12);
            redCircle.setFill(Color.RED);
            redCircle.setOpacity(0.2);
            StackPane.setAlignment(redCircle, Pos.TOP_RIGHT);
            StackPane.setMargin(redCircle, new Insets(6, 5, 0, 0));
            this.getChildren().addAll(background, redCircle, dayLabel, entryBox);
        } else {
            this.getChildren().addAll(background, dayLabel, entryBox);
        }

        this.setOnMouseClicked(e -> promptNewEntry());
    }

    public void addMonthTag(String monthAbbreviation) {
        Label tag = new Label(monthAbbreviation);
        tag.setPadding(new Insets(10, 0, 0, 10));
        StackPane.setAlignment(tag, Pos.TOP_LEFT);
        this.getChildren().add(tag);
    }

    private void promptNewEntry() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Neuer Eintrag");
        dialog.setHeaderText("Task für den " + day + ". "+ months[month] + " eingeben:");
        dialog.setContentText("Eintrag:");

        dialog.showAndWait().ifPresent(this::addEntry);
    }

    private void addEntry(String text) {
        entries.add(text);
        LocalDate date = LocalDate.of(year, month, day);
        String isoDate = date.toString(); // YYYY-MM-DD
        String formattedForServer = isoDate + "|" + text;
        CalenderEntry entryView = new CalenderEntry(text, formattedForServer);
        entryBox.getChildren().add(entryView);
        System.out.println("New Task: " + formattedForServer);
        NetworkManager.sendTask(formattedForServer);
    }

    public void loadEntry(String text) {
        entries.add(text);
        String isoDate = LocalDate.of(year, month, day).toString();
        String full = isoDate + "|" + text;
        CalenderEntry entryView = new CalenderEntry(text, full);
        entryBox.getChildren().add(entryView);
    }
}
