package ru.kstovoservice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.net.URL;
import java.util.ResourceBundle;


// для тестирования какого-то извращения создал этот интерфейс
interface Test {
    void te ();
}


//ссылка на этот класс идёт в fxml файле MainScence.fxml

public class Controller implements Initializable {
    @FXML
    //некоторые элементы меню (тут нужно явное именование объекта, например, closeMenu, так мы при инициализации можно определить его Event Handling (обработчик прерываний)
    public VBox mainMenu;
    public MenuItem aboutMenu;
    public MenuItem closeMenu;
    public Button testButton;
    public ListView posList;
    public Button TestButton1;
    public MenuItem delString;
    public MenuItem setString;
    public MenuItem editString;

    public void initialize (URL location, ResourceBundle resources) {
        //poslist init
        posList.setPrefSize(550, 300);
        posList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        posList.setEditable(true);
        ObservableList<String> items = FXCollections.<String>observableArrayList();
        for (int i = 0; i < 5; i++) {
            items.add(new Test() {
                public void te () {
                }
            }.toString()); // anonimus inner class
        }
        posList.setItems(items);
        items.addAll("Строка 1", "Строка 2");

        final ObservableList names =
                FXCollections.observableArrayList();
        names.addAll(
                "Adam", "Alex", "Alfred", "Albert",
                "Brenda", "Connie", "Derek", "Donny",
                "Lynne", "Myrtle", "Rose", "Rudolph",
                "Tony", "Trudy", "Williams", "Zach"
        );
        posList.setCellFactory(ComboBoxListCell.forListView(names));
        // postlist init end

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
                System.out.println((String) i);
            }
        }
    }

    public void testButtonAction (ActionEvent event) {
      ObservableList items = posList.getItems();
      System.out.println(items);

    }

    public void delStringAction (ActionEvent event) {
        ObservableList<String> items = posList.getItems();
        items.remove(posList.getSelectionModel().getSelectedItem());

    }

    public void addStringAction (ActionEvent event) {
        ObservableList<String> items = posList.getItems();
        items.add("Новый магазин...");
        posList.getSelectionModel().clearSelection();
        posList.getSelectionModel().selectLast();
        //posList.setItems(items);
    }

    public void editStringAction (ActionEvent event)  throws Exception {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("ShopSetupScene.fxml"));
        //stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Редактирование записи о магазине...");
        stage.show();
    }

    public void aboutMenuAction (ActionEvent event) {
        Label label = new Label("(c) ООО \"Кстовоторгсервис\". Версия 1.0. \n Тел. +7 (83145) 9-06-98 \n г.Кстово, 2018");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(label);
        Scene aboutScene = new Scene(secondaryLayout, 330, 230);
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
