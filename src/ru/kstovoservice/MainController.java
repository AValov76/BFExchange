package ru.kstovoservice;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


//ссылка на этот класс идёт в fxml файле Main.fxml

public class MainController implements Initializable {


    private SetOfPOS data; //переменная для

    @FXML
    //некоторые элементы меню (при инициализации можно определить его Event Handling, что и сделано ниже
    public VBox mainMenu;
    public MenuItem aboutMenu;
    public MenuItem closeMenu;
    public Button repButton;
    public ListView posList;

    // меню
    public MenuItem delString;
    public MenuItem setString;
    public MenuItem editString;
    public Label label1; // c
    public Label label2; // по
    public Label labelRep;  // результат обмена

    public void initialize (URL location, ResourceBundle resources) {

        // просто еще один способ описывать Event Handling это так называемое Lambda Expression
        closeMenu.setOnAction(event -> {
            // Закрываем приложение
            System.out.println("Close programm...");
            Platform.exit();
        });

        // инициализация модели_данных
        try {
            data = new Model();
           // System.out.println(data.listPOS()[0]);
            posListInitialization();

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        posList.setPrefSize(550, 300);
        posList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        posList.setEditable(true);
    }

    private void posListInitialization () {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll(data.listPOS());
        posList.setItems(items.sorted());
        posList.getSelectionModel().selectLast();

    }
    // Action зона
    public void testButtonAction (ActionEvent event) throws ParserConfigurationException, IOException, SAXException {
        // список выделенного
        Model s = new Model();
    }

    public void repButtonAction (ActionEvent event) {
        labelRep.setText("Тест кнопки Загрузить");
        labelRep.setVisible(true);

    }

    public void delStringAction (ActionEvent event) {
        ObservableList<String> items = posList.getItems();
        items.remove(posList.getSelectionModel().getSelectedItem());

    }

    public void addStringAction (ActionEvent event) {
        data.addNewPOS();
        posListInitialization();
        //System.out.println(data.listPOS()[1]);
        posList.getSelectionModel().clearSelection();
        posList.getSelectionModel().selectLast();
    }

    public void editStringAction (ActionEvent event) throws Exception { // пришлось дописать "throws Exception" так как иначе не работало
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("POS.fxml"));
        Parent root = loader.load();
        //Parent root = FXMLLoader.load(getClass().getResource("POS.fxml"));
        POSController posController = loader.getController(); //получаем контроллер для второй формы
        String s = (String)posList.getSelectionModel().getSelectedItem();
        //System.out.println(s);
        posController.initKV(data.getKV(s)); // передаем необходимые параметры
        stage.setScene(new Scene(root, 560, 272));
        stage.setTitle("Редактирование настройки обмена текущей кассы");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
/* Гы, работает, но с какими-то ошибками
        Stage stage = (Stage) mainMenu.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("POS.fxml"));
        stage.setScene(new Scene(root, 560, 272));
        stage.setTitle("Редактирование настройки обмена текущей кассы");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
*/
    }

    public void aboutMenuAction (ActionEvent event) throws Exception {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("About.fxml")); // базовый класс для всех узлов у которых есть потомки на сцене
        stage.setScene(new Scene(root, 500, 100));
        stage.setTitle("О программе...");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

    }

}
