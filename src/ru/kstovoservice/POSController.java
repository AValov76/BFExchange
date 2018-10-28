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
    String[] kv = new String[5]; //данные POS

    @FXML
    public AnchorPane mainAnchorPane;
    public TextField dirExchange, posName, flag, rep;
    public Button dirButton, cancelButton, okButton;
    public ChoiceBox frontChoiceBox;

    public void initialize (URL location, ResourceBundle resources) {
        //Кнопка выбора папки обмена
        dirButton.setOnAction(event -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            final Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            File file = directoryChooser.showDialog(stage);
            if (file != null) {
                dirExchange.setText(file.toString());
            }
        });
        //initPOS();
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

    private void initPOS () {
        posName.setText(kv[0]);
        dirExchange.setText(kv[1]);
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll("Атол", "Штрих");
        frontChoiceBox.setItems(items);
        if (kv[2].equals("Атол")) {
            frontChoiceBox.getSelectionModel().selectFirst();
        } else {
           frontChoiceBox.getSelectionModel().selectLast();
        }
        rep.setText(kv[3]);
        flag.setText(kv[4]);
    }
    public void initKV(String[] kv){
        this.kv=kv;
        initPOS();
    }

}
