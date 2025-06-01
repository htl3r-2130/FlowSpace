package com.example.flowspace_projectfolder;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class SearchPopup {

    private final Pane overlay;
    private final VBox popup;

    private final TextField searchField;
    private final ListView<String> resultList;

    public SearchPopup(StackPane root) {

        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setVisible(false);
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        popup = new VBox(15);
        popup.setStyle("-fx-background-color: #d0ddff; -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");
        popup.setPrefSize(350, 400);
        popup.setMaxSize(350, 400);
        popup.setMinSize(350, 400);
        popup.setVisible(false);

        // Suchfeld und Suchbutton
        searchField = new TextField();
        searchField.setPromptText("Suche...");
        searchField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");

        Button searchBtn = new Button("Suchen");
        styleOrangeButton(searchBtn);

        HBox searchBox = new HBox(10, searchField, searchBtn);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Ergebnisliste
        resultList = new ListView<>();
        resultList.setPrefHeight(250);

        // Schließen-Button
        Button closeBtn = new Button("Schließen");
        styleOrangeButton(closeBtn);

        popup.getChildren().addAll(searchBox, resultList, closeBtn);
        popup.setAlignment(Pos.TOP_CENTER);

        StackPane.setAlignment(popup, Pos.CENTER);

        root.getChildren().addAll(overlay, popup);

        // Schließen: Overlay oder Schließen-Button
        closeBtn.setOnAction(e -> hide());
        overlay.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> hide());

        // Suchen-Button klick
        searchBtn.setOnAction(e -> {
            String input = searchField.getText();
            List<String> matches = searchTasksWithRegex(input);
            resultList.getItems().setAll(matches);
        });
    }

    private void styleOrangeButton(Button btn) {
        btn.setStyle(
                "-fx-background-color: orange;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;"
        );
    }

    public void show() {
        searchField.clear();
        resultList.getItems().clear();
        overlay.setVisible(true);
        popup.setVisible(true);
    }

    public void hide() {
        overlay.setVisible(false);
        popup.setVisible(false);
    }


    private List<String> searchTasksWithRegex(String regex) {
        String[] everyTask = String.valueOf(NetworkManager.loadUserTasks()).split(",");
        List<String> matches = new java.util.ArrayList<>();

        for (String task : everyTask) {
            String[] parts = task.split("\\|", 2);
            if (parts.length == 2) {
                String date = parts[0].replace("[", "").replace("]", "").trim();
                String name = parts[1].replace("[", "").replace("]", "").trim();

                if (name.matches(".*" + regex + ".*")) {
                    // Datum-Format: JJJJ-MM-TT → TT.MM.JJJJ
                    String[] dateParts = date.split("-");
                    String formattedDate = dateParts[2] + "." + dateParts[1] + "." + dateParts[0];
                    String output = " " + name + " ist am " + formattedDate + " zu erledigen";
                    matches.add(output);
                }
            }
        }

        if (matches.isEmpty()) {
            matches.add("Keine Treffer gefunden.");
        }

        return matches;
    }

}
