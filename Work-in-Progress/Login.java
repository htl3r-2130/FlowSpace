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

public class Login extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Flowspace - Login");
        stage.getIcons().add(new Image("file:resources/icon.png"));
        stage.setWidth(500);
        stage.setHeight(500);
        stage.centerOnScreen();

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #eef2ff;");
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
        loginButton.setDefaultButton(true);

        Button signupButton = new Button("Noch kein Konto? Registrieren");

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
                    try {
                        HomeView calenderView = new HomeView();
                        calenderView.start(new Stage());
                        stage.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    statusLabel.setText("Login fehlgeschlagen");
                }
            } else {
                statusLabel.setText("Bitte Benutzername und Passwort eingeben.");
            }
        });

        signupButton.setOnAction(e -> {
            signupButton.setOnAction(f -> Signup.show(stage));
        });

        VBox formBox = new VBox(10);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setMaxWidth(300);
        HBox validationBox = new HBox(10);
        validationBox.getChildren().addAll(signupButton, loginButton);
        formBox.getChildren().addAll(userLabel, usernameField, pwLabel, passwordField, validationBox, statusLabel);

        root.getChildren().addAll(logo, formBox);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}