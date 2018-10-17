package ru.kstovotorgservice;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


//ссылка на этот класс идёт в fxml файле MainScence.fxml
interface test {
}

public class Controller implements Initializable {
    @FXML
    //некоторые элементы меню (тут нужено явное именование объекта, например, closeMenu, так мы при инициализации определяем его Event Handling (обработчик прерываний)
    public VBox mainMenu;
    public MenuItem aboutMenu;
    public MenuItem closeMenu;
    public Button testButton;
    public ListView posList;
    public Button TestButton1;

    public void initialize (URL location, ResourceBundle resources) {
        posList.setPrefWidth(500);
        posList.setPrefHeight(300);
        posList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // просто еще один способ описывать Event Handling это так называемое Lambda Expression
        closeMenu.setOnAction(event -> {
            // Закрываем приложение
            System.out.println("Close programm...");
            Platform.exit();
        });
    }

    // Action зона
    public void testButtonAction1 (ActionEvent event) {
        // список выделенного
        if (posList.getSelectionModel().getSelectedIndex() >= 0) {
            for (Object i :
                    posList.getSelectionModel().getSelectedItems()) {
                System.out.println((String)i);
            }
        }
    }

    public void testButtonAction (ActionEvent event) {

        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < 20; i++) {
            items.add(new test() {
            }.toString()); // anonimus inner class
        }
        posList.setItems(items);

    }

    public void aboutMenuAction (ActionEvent event) {
        Label label = new Label("(c) ООО \"Кстовоторгсервис\". Версия 1.0. \n Тел. +7 (83145) 9-06-98 \n г.Кстово, 2018");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(label);
        Scene aboutScene = new Scene(secondaryLayout, 230, 130);
        // New window (Stage)
        Stage aboutWindow = new Stage();
        aboutWindow.setTitle("О программе");
        aboutWindow.setScene(aboutScene);
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
