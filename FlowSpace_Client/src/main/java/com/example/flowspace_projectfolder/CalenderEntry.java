package com.example.flowspace_projectfolder;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

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
            TextInputDialog dialog = new TextInputDialog(text);
            dialog.setTitle("Eintrag bearbeiten");
            dialog.setHeaderText("Bearbeite deinen Task:");
            dialog.setContentText("Neuer Text:");

            dialog.showAndWait().ifPresent(newText -> {
                if (!newText.trim().isEmpty()) {
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
            });
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
