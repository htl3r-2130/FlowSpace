package com.example.flowspace_projectfolder;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    public CalendarDayCell(int day, int month, int year, boolean isToday, Stage stage) {
        this.day = day;
        this.month = month;
        this.year = year;

        Region background = new Region();
        background.setPrefSize(200, 200);
        background.setStyle("-fx-background-color: #d0ddff;");

        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setPadding(new Insets(10));
        StackPane.setAlignment(dayLabel, Pos.TOP_RIGHT);
        entryBox.setSpacing(5);
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

        this.setOnMouseClicked(e -> promptNewEntry(stage));
    }

    public void addMonthTag(String monthAbbreviation) {
        Label tag = new Label(monthAbbreviation);
        tag.setPadding(new Insets(10, 0, 0, 10));
        StackPane.setAlignment(tag, Pos.TOP_LEFT);
        this.getChildren().add(tag);
    }

    private void promptNewEntry(Stage owner) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(owner);
        popupStage.setTitle("Neuer Eintrag");

        Label header = new Label("Task für den " + day + ". " + months[month] + " eingeben:");
        TextField taskField = new TextField();
        taskField.setPromptText("Task");

        Label tagLabel = new Label("Typ:");
        ComboBox<String> tagDropdown = new ComboBox<>();
        tagDropdown.getItems().addAll("kein Typ", "Termin", "Abgabe", "Veranstaltung");
        tagDropdown.setValue("kein Typ");

        // Neues Notizfeld
        Label noteLabel = new Label("Notiz:");
        TextArea noteField = new TextArea();
        noteField.setPromptText("Notiz hier eingeben...");
        noteField.setPrefRowCount(3);

        Button okButton = new Button("Hinzufügen");
        Button cancelButton = new Button("Abbrechen");

        okButton.setOnAction(e -> {
            String entry = taskField.getText().trim();
            String tag = tagDropdown.getValue();
            String note = noteField.getText().trim();
            if (!entry.isEmpty()) {
                // Notiz wird nur an den Server gesendet, nicht in der Anzeige gezeigt
                addEntry(entry + "|" + tag + "|" + note);
            }
            popupStage.close();
        });

        cancelButton.setOnAction(e -> popupStage.close());

        HBox buttons = new HBox(10, okButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15,
                header,
                taskField,
                tagLabel,
                tagDropdown,
                noteLabel,   // Notiz Label
                noteField,   // Notiz TextArea
                buttons
        );
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #d0ddff; -fx-background-radius: 10; -fx-padding: 20;");

        Scene scene = new Scene(layout, 350, 300);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void addEntry(String text) {
        entries.add(text);

        // text enthält: taskName|tag|note
        String[] parts = text.split("\\|");
        String taskName = parts[0];
        String tag = parts[1];

        LocalDate date = LocalDate.of(year, month, day);
        String isoDate = date.toString();
        String formattedForServer = isoDate + "|" + text;

        CalenderEntry entryView = new CalenderEntry(taskName, tag, formattedForServer);
        entryBox.getChildren().add(entryView);

        System.out.println("New Task: " + formattedForServer);
        NetworkManager.sendTask(formattedForServer);
    }


    public void loadEntry(String text) {
        entries.add(text);

        String[] parts = text.split("\\|");
        String taskName = parts[0];
        String tag = parts[1];

        String isoDate = LocalDate.of(year, month, day).toString();
        String full = isoDate + "|" + text;
        entryBox.getChildren().add(new CalenderEntry(taskName, tag, full));
    }
}