package ru.kstovotorgservice;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

//ссылка на этот класс идёт в fxml файле MainScence.fxml
public class Controller implements Initializable {
    @FXML
    //некоторые элементы меню (тут нужено явное именование объекта, например, closeMenu, так мы при инициализации определяем его Event Handling (обработчик прерываний)
    public VBox mainMenu;
    public MenuItem aboutMenu;
    public MenuItem closeMenu;

    public void initialize (URL location, ResourceBundle resources) {
        // просто еще один способ описывать Event Handling это так называемое Lambda Expression
        closeMenu.setOnAction(event -> {
            // Закрываем приложение
            System.out.println("Close programm...");
            Platform.exit();
        });
    }

    public void aboutMenuAction (ActionEvent event) {
        System.out.println("About");
        Label label = new Label("(c) ООО \"Кстовоторгсервис\". Версия 1.0. \n Тел. +7 (83145) 9-06-98 \n г.Кстово, 2018");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(label);
        Scene secondScene = new Scene(secondaryLayout, 230, 130);
        // New window (Stage)
        Stage aboutWindow = new Stage();
        aboutWindow.setTitle("О программе");
        aboutWindow.setScene(secondScene);
        aboutWindow.initModality(Modality.APPLICATION_MODAL);
        aboutWindow.setResizable(false);
        aboutWindow.setWidth(300);
        aboutWindow.setMaxHeight(80);
        // Set position of second window, related to primary window.
        //aboutWindow.setX(primaryStage.getLayoutX() + 200);
        //aboutWindow.setY(mainVBox.getLayoutY() + 100);
        aboutWindow.show();
    }

}
