package com.example.flowspace_projectfolder;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Login extends Application {
    private CheckBox staySignedInBox = new CheckBox("Angemeldet bleiben");
    private static final File STAY_SIGNED_IN_FILE = new File("staySignedIn.txt");

    @Override
    public void start(Stage stage) {
        stage.setTitle("Flowspace - Login");
        stage.getIcons().add(new Image("file:resources/icon.png"));
        stage.setWidth(500);
        stage.setHeight(500);
        stage.centerOnScreen();

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #d0ddff; -fx-padding: 20;");
        root.setPadding(new Insets(40));

        ImageView logo = new ImageView(new Image("file:resources/icon.png"));
        logo.setFitWidth(100);
        logo.setPreserveRatio(true);

        Label userLabel = new Label("Benutzername:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Benutzername");

        Label pwLabel = new Label("Passwort:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: orange;" + "-fx-text-fill: white;" + "-fx-background-radius: 5;" + "-fx-border-radius: 5;");
        loginButton.setDefaultButton(true);

        Button signupButton = new Button("Noch kein Konto? Registrieren");
        signupButton.setStyle("-fx-background-color: orange;" + "-fx-text-fill: white;" + "-fx-background-radius: 5;" + "-fx-border-radius: 5;");

        VBox formBox = new VBox(10);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setMaxWidth(300);

        HBox validationBox = new HBox(10);
        validationBox.getChildren().addAll(signupButton, loginButton);

        formBox.getChildren().addAll(userLabel, usernameField, pwLabel, passwordField, staySignedInBox, validationBox, statusLabel);
        root.getChildren().addAll(logo, formBox);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        tryAutoLogin(stage);

        loginButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pw = passwordField.getText();

            if (!user.isEmpty() && !pw.isEmpty()) {
                boolean connected = NetworkManager.connect();
                if (!connected) {
                    statusLabel.setText("Verbindung zum Server fehlgeschlagen.");
                    return;
                }
                boolean success = NetworkManager.login(user, pw);
                if (success) {
                    if (staySignedInBox.isSelected()) {
                        try {
                            Files.writeString(STAY_SIGNED_IN_FILE.toPath(), user + "=" + pw);
                        } catch (IOException ex) {
                            System.err.println("Fehler beim Schreiben der staySignedIn.txt");
                        }
                    }
                    openHome(stage);
                } else {
                    statusLabel.setText("Login fehlgeschlagen");
                }
            } else {
                statusLabel.setText("Bitte Benutzername und Passwort eingeben.");
            }
        });

        signupButton.setOnAction(f -> Signup.show(stage));
    }

    private void tryAutoLogin(Stage stage) {
        if (STAY_SIGNED_IN_FILE.exists()) {
            try {
                String content = Files.readString(STAY_SIGNED_IN_FILE.toPath());
                String[] parts = content.split("=", 2);
                if (parts.length == 2) {
                    String user = parts[0];
                    String pw = parts[1];
                    boolean connected = NetworkManager.connect();
                    if (connected && NetworkManager.login(user, pw)) {
                        openHome(stage);
                    }
                }
            } catch (IOException e) {
                System.err.println("Fehler beim automatischen Login.");
            }
        }
    }

    private void openHome(Stage loginStage) {
        try {
            HomeView home = new HomeView();
            home.start(new Stage());
            loginStage.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void clearStaySignedIn() {
        if (STAY_SIGNED_IN_FILE.exists()) {
            STAY_SIGNED_IN_FILE.delete();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
