package ru.kstovotorgservice;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Controller implements Initializable {
    @FXML
    public VBox mainMenu;
    public MenuItem aboutMenu;
    public MenuItem closeMenu;

    public void initialize (URL location, ResourceBundle resources) {
        // TODO (don't really need to do anything here).
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

    public void closeProg(ActionEvent event){
        // Закрываем приложение
        String[] options = { "Да", "Нет!" };
        System.out.println("Close programm...");
    }
}
