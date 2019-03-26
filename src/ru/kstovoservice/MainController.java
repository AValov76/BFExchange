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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//ссылка на этот класс идёт в fxml файле Main.fxml

public class MainController implements Initializable {


    public MainController mainController;
    public SetOfPOS data; //набор данных по каждому POS

    @FXML
    //некоторые элементы меню (при инициализации можно определить его Event Handling, что и сделано ниже
    public VBox mainMenu;
    public MenuItem aboutMenu;
    public MenuItem closeMenu;
    public Button repButton;
    public MenuItem delString;
    public MenuItem setString;
    public MenuItem editString;
    // остальные узлы сцены
    public ListView posList;
    public Label label1; // c
    public Label label2; // по
    public Label labelRep;  // результат обмена
    public DatePicker dateFrom;
    public DatePicker dateTo;
    public ChoiceBox typeOfRepChoiceBox;


    public void initialize(URL location, ResourceBundle resources) {

        // просто еще один способ описывать Event Handling это так называемое Lambda Expression
        closeMenu.setOnAction(event -> {
            // Закрываем приложение
            programExit();
        });

        // инициализация модели_данных
        try {
            data = new Model();
            // System.out.println(data.listPOS()[0]);
            posListInitialization();

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        }

        posList.setPrefSize(550, 300);
        posList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        posList.setEditable(true);
        // заполнение значениями по умолчанию
        dateFrom.setValue(LocalDate.now().minusDays(1));
        dateTo.setValue(LocalDate.now().minusDays(1));
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll("по дням", "по сменам");
        typeOfRepChoiceBox.setItems(items);
        typeOfRepChoiceBox.getSelectionModel().selectFirst();
        //
    }

    private void programExit() {
        try {
            data.saveAllDataToFile();
            System.out.println("Close program...");
            Platform.exit();
        } catch (ParserConfigurationException ex) {
        }
    }

    private void posListInitialization() {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll(data.getListPOS());
        posList.setItems(items.sorted());
        posList.getSelectionModel().selectLast();

    }

    // ожидание ответа с кассы (отчета с ККТ)
    private void repWait() {

    }

    private void repRequestControl() throws Error {

        if (typeOfRepChoiceBox.getSelectionModel().isSelected(0)) {
            if (dateFrom.getValue() == null) {
                throw new Error("Проверьте дату начала отчета!");
            } else if (dateTo.getValue() == null) {
                throw new Error("Проверьте дату конца отчета!");
            }
            if (dateFrom.getValue().compareTo(dateTo.getValue()) > 0) {
                throw new Error("Дата начала отчета должна быть меньше даты конца!"); //);
            }
        } else {
            throw new Error("Запрос отчетов по номерам не реализован!");
        }
    }

    // формирование файла-запроса отчета с POS-терминала, на котором фокус
    private void repFileRequest(String dateF, String dateT, String pathFlag, String nameFlagFile) throws IOException {
        FileWriter fileWriter;
        fileWriter = new FileWriter(pathFlag+"\\"+nameFlagFile);
        fileWriter.write("$$$TRANSACTIONSBYDATERANGE");
        fileWriter.write("\r\n");
        fileWriter.write(dateF + ";" + dateT);
        fileWriter.write("\r\n");
        fileWriter.close();
    }


    // Action зона

    public void testButtonAction(ActionEvent event) throws ParserConfigurationException, IOException, SAXException {
        data.addNewPOS();
        posListInitialization();
        Thread tr1 = new Thread(new R_ex());
        tr1.start();

    }

    public void delStringAction(ActionEvent event) {
        data.removePOS((String) posList.getSelectionModel().getSelectedItem());
        posListInitialization();
    }

    public void addStringAction(ActionEvent event) {
        data.addNewPOS();
        initList();
    }

    public void initList() {
        posListInitialization();
        posList.getSelectionModel().clearSelection();
        posList.getSelectionModel().selectLast();
    }

    public void editStringAction(ActionEvent event) throws Exception { // пришлось дописать "throws Exception" так как иначе не работало
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("POS.fxml"));
        Parent root = loader.load();
        POSController posController = loader.getController(); //получаем контроллер для второй формы
        posController.initPOS(mainController); // передаем необходимые параметры
        stage.setScene(new Scene(root, 560, 410));
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

    public void aboutMenuAction(ActionEvent event) throws Exception {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("About.fxml")); // базовый класс для всех узлов у которых есть потомки на сцене
        stage.setScene(new Scene(root, 500, 100));
        stage.setTitle("О программе...");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    // возвращает массив с данными кассы которая в фокусе
    public String[] getSelectedPOSData() {
        return data.getKV((String) posList.getSelectionModel().getSelectedItem());
    }

    // обработка запроса отчета с кассы
    public void repRequestButtonAction(ActionEvent event) {

        try {
            //проверка на корректность перед запросом
            repRequestControl();
            //формирование файла запроса
            SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            System.out.println(newDateFormat.format(oldDateFormat.parse(dateFrom.getValue().toString())));
            repFileRequest(newDateFormat.format(oldDateFormat.parse(dateFrom.getValue().toString())), newDateFormat.format(oldDateFormat.parse(dateTo.getValue().toString())),getSelectedPOSData()[1] ,getSelectedPOSData()[4]);
            // ожидание загрузки отчета
            labelRep.setVisible(true);
            labelRep.setTextFill(Color.BLACK);
            labelRep.setText("Ожидаем получения отчета за выбранный период");
            repWait();
        } catch (Throwable error) {
            labelRep.setTextFill(Color.RED);
            labelRep.setText(error.getMessage());
        }
    }
}
class R_ex implements java.lang.Runnable  {
    @Override
    public void run () {
        System.out.println("поток тест");
    }
}