package ru.kstovoservice;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.io.File;
import java.net.URL;
import java.util.*;

//ссылка на этот класс идёт в fxml файле Shop.fxml

public class ShopController implements Initializable {
    @FXML
    public AnchorPane mainAnchorPane;
    public Button buttonDir;
    public TextField dirExchange;
    public TextField rep;
    public TextField flag;
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
        items.addAll("Атол","Штрих");
        frontChoiceBox.setItems(items);
        frontChoiceBox.getSelectionModel().selectFirst();
        //выбор типа Фронт-офиса кассы
        frontChoiceBox.setOnAction(event -> {
        });
        rep.setText("report.rep");
        flag.setText("report.flg");

    }

}
