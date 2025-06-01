package com.example.flowspace_projectfolder;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AccountCenter {

    public static void show(Stage owner, Stage parentStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Account Center");
        dialog.initOwner(owner);
        dialog.setResizable(false);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #eef2ff;");

        // --- Header ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        ImageView icon = new ImageView(new Image("file:resources/user.png"));
        icon.setFitWidth(30);
        icon.setPreserveRatio(true);

        Label usernameLabel = new Label(NetworkManager.getCurrentUser());
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        HBox.setHgrow(usernameLabel, Priority.ALWAYS);
        usernameLabel.setMaxWidth(Double.MAX_VALUE);
        usernameLabel.setAlignment(Pos.CENTER);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            NetworkManager.disconnect();
            dialog.close();  // Schließt den Account Center Dialog
            parentStage.close(); // Schließt die HomeView Stage
            Stage loginStage = new Stage();  // Neue Stage für Login
            new Login().start(loginStage);   // Startet die Login-View
        });

        header.getChildren().addAll(icon, usernameLabel, logoutBtn);

        Label changePwLabel = new Label("Passwort ändern");
        changePwLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Neues Passwort");

        PasswordField pwConfirmField = new PasswordField();
        pwConfirmField.setPromptText("Passwort wiederholen");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        Button submitBtn = new Button("Passwort speichern");
        submitBtn.setOnAction(e -> {
            String pw = pwField.getText();
            String pwConfirm = pwConfirmField.getText();

            if (pw.isEmpty() || pwConfirm.isEmpty()) {
                statusLabel.setText("Bitte beide Felder ausfüllen.");
            } else if (!pw.equals(pwConfirm)) {
                statusLabel.setText("Passwörter stimmen nicht überein.");
            } else {
                boolean success = NetworkManager.changePassword(pw);
                statusLabel.setText(success ? "Passwort geändert!" : "Fehler beim Speichern.");
                statusLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                pwField.clear();
                pwConfirmField.clear();
            }
        });

        Button deleteBtn = new Button("Account löschen");
        deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            boolean confirmed = new Alert(Alert.AlertType.CONFIRMATION, "Wirklich löschen?", ButtonType.YES, ButtonType.NO)
                    .showAndWait().orElse(ButtonType.NO) == ButtonType.YES;

            if (confirmed) {
                boolean deleted = NetworkManager.deleteAccount();
                if (deleted) {
                    dialog.close();  // Schließt den Account Center Dialog
                    parentStage.close(); // Schließt die HomeView Stage
                    Stage loginStage = new Stage();  // Neue Stage für Login
                    new Login().start(loginStage);   // Startet die Login-View
                } else {
                    statusLabel.setText("Fehler beim Löschen.");
                }
            }
        });

        VBox pwBox = new VBox(10, changePwLabel, pwField, pwConfirmField, submitBtn);
        root.getChildren().addAll(header, pwBox, statusLabel, deleteBtn);

        Scene scene = new Scene(root, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }
}

