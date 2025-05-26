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
import static com.example.flowspace_projectfolder.CalenderView.months;

public class CalendarDayCell extends StackPane {
    private final int day;
    private final List<String> entries = new ArrayList<>();
    private final VBox entryBox = new VBox();
    private final int month;

    public CalendarDayCell(int day,int month, boolean isToday) {
        this.day = day;
        this.month = month;

        Region background = new Region();
        background.setPrefSize(100, 100);
        background.setStyle("-fx-background-color: #d0ddff;");

        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setTextFill(Color.GRAY);
        dayLabel.setPadding(new Insets(5));
        StackPane.setAlignment(dayLabel, Pos.TOP_RIGHT);

        entryBox.setSpacing(2);
        entryBox.setPadding(new Insets(30, 5, 5, 5));

        if (isToday) {
            Circle redCircle = new Circle(12);
            redCircle.setFill(Color.grayRgb(200));
            StackPane.setAlignment(redCircle, Pos.TOP_RIGHT);
            this.getChildren().addAll(background, redCircle, dayLabel, entryBox);
        } else {
            this.getChildren().addAll(background, dayLabel, entryBox);
        }

        this.setOnMouseClicked(e -> promptNewEntry());
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
        String taskText = text + ", " + day + "." + month + "." + LocalDate.now().getYear();
        CalenderEntry entryView = new CalenderEntry(text, taskText);
        entryBox.getChildren().add(entryView);
        System.out.println("Neuer Task: " + taskText);
        NetworkManager.sendTask(taskText);
    }

}
