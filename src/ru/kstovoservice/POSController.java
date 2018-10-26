package ru.kstovoservice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

//ссылка на этот класс идёт в fxml файле POS.fxml

public class POSController implements Initializable {

    @FXML
    public AnchorPane mainAnchorPane;
    public TextField dirExchange, posName, rep, flag;
    public Button dirButton, cancelButton,okButton;

    public ChoiceBox frontChoiceBox;

    public void initialize (URL location, ResourceBundle resources) {
        //Кнопка выбора папки обмена
        //cancelButton.setBlendMode();
        dirButton.setOnAction(event -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            final Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            File file = directoryChooser.showDialog(stage);
            if (file != null) {
                dirExchange.setText(file.toString());
            }
        });
        ObservableList items = FXCollections.observableArrayList();
        items.addAll("Атол", "Штрих");
        frontChoiceBox.setItems(items);
        frontChoiceBox.getSelectionModel().selectFirst();
        //выбор типа Фронт-офиса кассы
        frontChoiceBox.setOnAction(event -> {
        });
        cancelButton.setOnAction(event -> {
            System.out.println("Закрываем форму...");
            //With the exception of the root node of a scene graph, each node in a scene graph has a single parent and zero or more children
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
    }

}
