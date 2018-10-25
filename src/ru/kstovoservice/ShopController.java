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

//ссылка на этот класс идёт в fxml файле Shop.fxml

public class ShopController implements Initializable {

    @FXML
    public AnchorPane mainAnchorPane;
    public TextField dirExchange;
    public TextField posName;
    public TextField rep;
    public TextField flag;
    public Button buttonDir;
    public ChoiceBox frontChoiceBox;

    public void initialize (URL location, ResourceBundle resources) {
        //Кнопка выбора папки обмена
        buttonDir.setOnAction(event -> {
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
        rep.setText("report.rep");
        flag.setText("report.flg");

    }

}
