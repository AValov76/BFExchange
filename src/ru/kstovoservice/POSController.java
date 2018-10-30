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

    String[] kv = new String[5]; //данные POS, которыми обменивается окно "Редактирование настройки обмена текущей кассы" с главным окном
    private MainController mainController; //увы, ничего умнее для передачи измененных данных POS взад при закрытии окна не придумал
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

        //выбор типа Фронт-офиса кассы
        frontChoiceBox.setOnAction(event -> {
        });

        //кнопка отмены изменений
        cancelButton.setOnAction(event -> {
            //With the exception of the root node of a scene graph, each node in a scene graph has a single parent and zero or more children
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
        //кнопка принятия изменений
        okButton.setOnAction(event -> {
            //удаляем то что было на момент открытия формы из массива данных
            mainController.data.removePOS(kv[0]);
            //сохраняем новые данные из формы в массив
            setKV();
            //херачим этот массив в data главного контроллера
            String[] s= {kv[1],kv[2],kv[3],kv[4]};
            mainController.data.addPOS(kv[0],s);
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
        kv[0]=posName.getText();
        // dirExchange
        kv[1]=dirExchange.getText();
        //frontChoiceBox
        kv[2]=frontChoiceBox.getSelectionModel().getSelectedItem().toString();
        // rep
        kv[3]=rep.getText();
        // flag
        kv[4]=flag.getText();
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
    }

    // метод для передачи данных по POS из главного окна в это
    public void initPOS (MainController mainController) {
        //передача данных из главного окна в это
        this.mainController = mainController;
        this.kv = mainController.getSelectedPOSData();
        // заполняем форму данными из массива kv[] (данные туда были записаны при инициализации окна методом MainController.editStringAction так posController.initKV(data.getKV(s))
        setPOS();
    }

}
