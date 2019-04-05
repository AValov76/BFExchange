package ru.kstovoservice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

    String[] kv = new String[8]; //данные POS, которыми обменивается окно "Редактирование настройки обмена текущей кассы" с главным окном
    private MainController mainController; //увы, ничего умнее для передачи измененных данных POS взад при закрытии окна не придумал
    @FXML
    public AnchorPane mainAnchorPane;
    public TextField dirExchange,
            dirExchangeIP,
            dirExchangeOOO,
            posName,
            flag,
            rep,
            goodsIPFileNameTextField,
            repIPFileNameTextField,
            goodsOOOFileNameTextField,
            repOOOFileNameTextField,
            goodsPOSFileNameTextField,
            skuModTextField,
            ipGroupTextField,
            oooGroupTextField,
            repWaitTimeTextField;

    public Button dirButton,
            dirButtonIP,
            dirButtonOOO,
            cancelButton,
            okButton;
    public ChoiceBox frontChoiceBox;
    public CheckBox checkBox;

    public void initialize (URL location, ResourceBundle resources) {

        goodsIPFileNameTextField.setText(Sync1C.GOODS_IP_FILENAME);
        repIPFileNameTextField.setText(Sync1C.REP_IP_FILENAME);
        goodsOOOFileNameTextField.setText(Sync1C.GOODS_OOO_FILENAME);
        repOOOFileNameTextField.setText(Sync1C.REP_OOO_FILENAME);
        goodsPOSFileNameTextField.setText(Sync1C.GOODS_POS_FILENAME);
        skuModTextField.setText(Sync1C.SKU_MOD);
        ipGroupTextField.setText(Sync1C.IP_PRINTGROUP_CODE);
        oooGroupTextField.setText(Sync1C.OOO_PRINTGROUP_CODE);
        repWaitTimeTextField.setText(Integer.toString(Sync1C.REPWAITTIME));

        //выбор типа Фронт-офиса кассы
        frontChoiceBox.setOnAction(event -> {
        });

        //кнопка отмены изменений
        cancelButton.setOnAction(event -> {
            //With the exception of the root node of a scene graph, each node in a scene graph has a single parent and zero or more children
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });

        //кнопка активации checkbox
        checkBox.setOnAction(event -> {
        });


        //кнопка принятия изменений
        okButton.setOnAction(event -> {
            //удаляем то что было на момент открытия формы из массива данных
            mainController.data.removePOS(kv[0]);
            //сохраняем новые данные из формы в массив
            setKV();
            //херачим этот массив в data главного контроллера
            String[] s = {kv[1], kv[2], kv[3], kv[4], kv[5], kv[6], kv[7]};
            mainController.data.addPOS(kv[0], s);
            //перегружаем список
            mainController.initList();
            //With the exception of the root node of a scene graph, each node in a scene graph has a single parent and zero or more children
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
    }

    //записывает данные из формы в массив (временное хранилище)
    private void setKV () {
        // posName
        kv[0] = posName.getText();
        // dirExchange
        kv[1] = dirExchange.getText();
        //frontChoiceBox
        kv[2] = frontChoiceBox.getSelectionModel().getSelectedItem().toString();
        // rep
        kv[3] = rep.getText();
        // flag
        kv[4] = flag.getText();
        // checkBoxIPOOO
        kv[5] = (checkBox.isSelected()) ? "1" : "0";
        // dirExchangeIP
        kv[6] = dirExchangeIP.getText();
        // dirExchangeOOO
        kv[7] = dirExchangeOOO.getText();
    }

    private void setPOS () {
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
        checkBox.setSelected((kv[5]).equals("1"));
        dirExchangeIP.setText(kv[6]);
        dirExchangeOOO.setText(kv[7]);
    }

    // метод для передачи данных по POS из главного окна в это
    public void initPOS (MainController mainController) {
        //передача данных из главного окна в это
        this.mainController = mainController;
        this.kv = mainController.getSelectedPOSData();
        // заполняем форму данными из массива kv[] (данные туда были записаны при инициализации окна методом MainController.editStringAction так posController.initKV(data.getKV(s))
        setPOS();
    }

    // Action зона
    // обработчик кнопки выбора папки (одинаков для всех трех кнопок
    public void dirButtonAction (ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            if (event.getTarget().equals(dirButtonIP)) dirExchangeIP.setText(file.toString());
            if (event.getTarget().equals(dirButtonOOO)) dirExchangeOOO.setText(file.toString());
            if (event.getTarget().equals(dirButton)) dirExchange.setText(file.toString());
        }

    }
}
