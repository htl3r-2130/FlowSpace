package com.example.flowspace_projectfolder;

import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class CalenderEntry extends HBox {
    private String text;
    private String taskText;

    public CalenderEntry(String text, String taskText) {
        this.text = text;
        this.taskText = taskText;
        setPadding(new Insets(10));
        setBackground(new Background(new BackgroundFill(Color.web("#E4EBFF"), new CornerRadii(4), Insets.EMPTY)));

        Label label = new Label(text.length() > 15 ? text.substring(0, 15) + "..." : text);
        label.setTooltip(new Tooltip(text));
        getChildren().add(label);

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
