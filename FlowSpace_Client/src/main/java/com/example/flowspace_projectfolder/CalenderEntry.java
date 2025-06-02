package com.example.flowspace_projectfolder;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CalenderEntry extends HBox {
    private String text;
    private String taskText;
    private final Label label;

    public CalenderEntry(String text, String taskText) {
        this.text = text;
        this.taskText = taskText;
        setPadding(new Insets(10));
        setBackground(new Background(new BackgroundFill(Color.web("#E4EBFF"), new CornerRadii(4), Insets.EMPTY)));

        label = new Label(shortenText(text));
        label.setTooltip(new Tooltip(text));
        getChildren().add(label);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Bearbeiten");

        editItem.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.WINDOW_MODAL);

            popupStage.setTitle("Eintrag bearbeiten");

            Label header = new Label("Bearbeite deinen Task:");
            TextField inputField = new TextField(text); // "text" ist der aktuelle Eintrag
            inputField.setPromptText("Neuer Text");

            Button okButton = new Button("Speichern");
            Button cancelButton = new Button("Abbrechen");

            okButton.setOnAction(ev -> {
                String newText = inputField.getText().trim();
                if (!newText.isEmpty()) {
                    // Alte Aufgabe löschen
                    NetworkManager.deleteTask(taskText);

                    // Neue Daten setzen
                    this.text = newText;
                    String date = taskText.split("\\|")[0];
                    this.taskText = date + "|" + newText;

                    // UI aktualisieren
                    label.setText(shortenText(newText));
                    label.setTooltip(new Tooltip(newText));

                    // Neue Aufgabe senden
                    NetworkManager.sendTask(this.taskText);
                    System.out.println("Eintrag aktualisiert: " + this.taskText);
                }
                popupStage.close();
            });
            cancelButton.setOnAction(ev -> popupStage.close());

            HBox buttons = new HBox(10, okButton, cancelButton);
            buttons.setAlignment(Pos.CENTER);

            VBox layout = new VBox(15, header, inputField, buttons);
            layout.setAlignment(Pos.CENTER);
            layout.setStyle("-fx-background-color: #d0ddff; -fx-background-radius: 10; -fx-padding: 20;");

            Scene scene = new Scene(layout, 350, 200);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            popupStage.setScene(scene);
            popupStage.showAndWait();
        });

        MenuItem deleteItem = new MenuItem("Löschen");
        deleteItem.setOnAction(e -> {
            if (getParent() instanceof Pane parent) {
                parent.getChildren().remove(this);
                System.out.println("Eintrag gelöscht: " + taskText);
                NetworkManager.deleteTask(this.taskText);
            }
        });

        contextMenu.getItems().addAll(editItem, deleteItem);
        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });
    }

    private String shortenText(String t) {
        return t.length() > 15 ? t.substring(0, 15) + "..." : t;
    }
}
