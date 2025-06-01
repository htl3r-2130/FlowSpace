package com.example.flowspace_projectfolder;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class CalenderEntry extends HBox {
    private String taskName;
    private String tag;
    private String taskText; // Format: date|task|tag
    public CalenderEntry(String taskName, String tag, String taskText) {
        this.taskName = taskName;
        this.tag = tag;
        this.taskText = taskText;


        setPadding(new Insets(10));
        setBackground(new Background(new BackgroundFill(Color.web("#E4EBFF"), new CornerRadii(4), Insets.EMPTY)));

        // Taskname anzeigen (ggf. gekürzt)
        Label taskLabel = new Label(taskName.length() > 15 ? taskName.substring(0, 15) + "..." : taskName);
        taskLabel.setTooltip(new Tooltip(taskName));
        getChildren().add(taskLabel);
        // Spacer hinzufügen, der den Platz dazwischen auffüllt
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);
        // Tag anzeigen (nur, falls nicht "kein Tag" oder leer)
        if (!tag.isEmpty() && !tag.equals("kein Typ")) {
            Label tagLabel = new Label(tag);
            tagLabel.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-padding: 2 5 2 5; -fx-background-radius: 5;");
            getChildren().add(tagLabel);
        }

        // Kontextmenü zum Löschen
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Löschen");
        deleteItem.setOnAction(e -> {
            if (getParent() instanceof Pane parent) {
                parent.getChildren().remove(this);
                System.out.println("Eintrag gelöscht: " + taskText);
                NetworkManager.deleteTask(this.taskText);
            }
        });
        contextMenu.getItems().addAll(deleteItem);

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });
    }

    public String getTaskText() {
        return taskText;
    }
}
