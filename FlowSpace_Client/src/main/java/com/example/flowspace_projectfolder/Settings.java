package com.example.flowspace_projectfolder;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.io.*;
import java.nio.file.*;

public class Settings {

    public static void show(Stage owner, Stage parentStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Einstellungen");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #eef2ff;");
        root.setAlignment(Pos.CENTER);

        Label statusLabel = new Label();

        // Config vom Server herunterladen
        Button downloadBtn = new Button("Config vom Server herunterladen");
        downloadBtn.setOnAction(e -> {
            String configContent = NetworkManager.downloadConfig();
            if (configContent != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName("server-config.txt");
                File file = fileChooser.showSaveDialog(dialog);
                if (file != null) {
                    try {
                        Files.writeString(file.toPath(), configContent);
                        statusLabel.setText("Config gespeichert.");
                    } catch (IOException ex) {
                        statusLabel.setText("Fehler beim Speichern.");
                    }
                }
            } else {
                statusLabel.setText("Download fehlgeschlagen.");
            }
        });

        // Config zum Server hochladen
        Button uploadBtn = new Button("Config zum Server hochladen");
        uploadBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Config-Datei auswählen");
            File file = chooser.showOpenDialog(dialog);
            if (file != null) {
                try {
                    String content = Files.readString(file.toPath());
                    boolean success = NetworkManager.uploadConfig(content);
                    if (success) {
                        statusLabel.setText("Config gespeichert.");
                        NetworkManager.disconnect();
                        dialog.close();
                        parentStage.close();
                        Stage loginStage = new Stage();
                        new Login().start(loginStage);
                    }
                    else {
                        statusLabel.setText("Fehler beim Speichern.");
                    }
                } catch (IOException ex) {
                    statusLabel.setText("Fehler beim Lesen der Datei.");
                }
            }
        });

        root.getChildren().addAll(downloadBtn, uploadBtn, statusLabel);
        Scene scene = new Scene(root, 400, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
