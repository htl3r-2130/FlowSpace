package com.example.flowspace_projectfolder;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Signup {

    public static void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.setTitle("Registrierung");
        dialog.initOwner(owner);
        dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        dialog.setResizable(false);

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(20));

        TextField userField = new TextField();
        PasswordField pwField = new PasswordField();
        PasswordField pwConfirmField = new PasswordField();

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        Button registerButton = new Button("Registrieren");

        registerButton.setOnAction(e -> {
            String user = userField.getText();
            String pw = pwField.getText();
            String pwConfirm = pwConfirmField.getText();

            if (user.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty()) {
                statusLabel.setText("Alle Felder ausfüllen.");
            } else if (!pw.equals(pwConfirm)) {
                statusLabel.setText("Passwörter stimmen nicht überein.");
            } else {
                boolean connected = NetworkManager.connect();
                if (!connected) {
                    statusLabel.setText("Serververbindung fehlgeschlagen.");
                    return;
                }

                boolean success = NetworkManager.signup(user, pw);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Erfolg");
                    alert.setHeaderText(null);
                    alert.setContentText("Registrierung erfolgreich!");
                    alert.initOwner(dialog);
                    alert.showAndWait();
                    dialog.close();
                } else {
                    statusLabel.setText("Benutzername bereits vergeben.");
                }
            }
        });

        root.getChildren().addAll(
                new Label("Benutzername:"), userField,
                new Label("Passwort:"), pwField,
                new Label("Passwort wiederholen:"), pwConfirmField,
                registerButton, statusLabel
        );
        Scene scene = new Scene(root, 300, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}

