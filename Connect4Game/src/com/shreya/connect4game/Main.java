package com.shreya.connect4game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void init() throws Exception {
        System.out.println("Initialize");
        super.init();
    }
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println("Start");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 4 Game");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    private MenuBar createMenu(){
        //File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> controller.resetGame());
        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGameGame = new MenuItem("Exit Game");
        exitGameGame.setOnAction(event -> endGame());
        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGameGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Game");
        aboutGame.setOnAction(event -> aboutConnect4Game());
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe  = new MenuItem("About Developer");
        aboutMe.setOnAction(event -> aboutDeveloper());
        helpMenu.getItems().addAll(aboutGame, separator, aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }
    private void aboutDeveloper() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Created by Shreya Singh Bais.");
        alert.setContentText("I am pursuing Software Engineering. I like to code, create and develop." +
                "I will try my best to become an excellent 'Software or Full Stack Web Developer' as soon as possible.");
        alert.show();
    }
    private void aboutConnect4Game() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect 4 Game");
        alert.setHeaderText("How to Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid."
                +" The pieces fall straight down, occupying the next available space within the column."
                +" The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs."
                +" Connect Four is a solved game."
                +" The first player can always win by playing the right moves.");
        alert.show();
    }
    private void endGame() {
        Platform.exit();
        System.exit(0);
    }
    @Override
    public void stop() throws Exception {
        System.out.println("Exit");
        super.stop();
    }
}
