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

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.application.Platform.runLater;

//ссылка на этот класс идёт в fxml файле Main.fxml

public class MainController implements Initializable {

    // оно щелкает часами в потоке
    private int timerClock = Sync1C.REPWAITTIME;
    // типа интерфейс данных (данные могут храниться по-разному, а интерфейс будет один и тот же)
    // этот интерфейс описан и реализован в классе Model.java
    public SetOfPOS data; //набор данных по каждому POS

    @FXML
    //некоторые элементы меню (при инициализации можно определить его Event Handling, что и сделано ниже
    public VBox mainMenu;
    public MenuItem aboutMenu;
    public MenuItem closeMenu;
    public Button repButton, rep1CButton;
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

        // просто еще один способ описывать Event Handling
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

    // обработка выхода из программе
    public void programExit() {
        try {
            data.saveAllDataToFile();
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
    private void repWait(String repFileName, String repFlagName) {
        // обработка запроса отчета в отдельном потоке
        Thread rep = new Thread(new MainController.RepReq(repFileName, repFlagName));    //Создание потока RepReq, ожидающий файл с отчетом
        rep.start();                //Запуск потока
    }

    // проверка перед формированием флага запроса отчета
    private void repRequestControl() throws Error {
        if (!getSelectedPOSData()[2].equals(Sync1C.ATOLPOS))
            throw new Error("Работа с кассами " + getSelectedPOSData()[2] + " не поддерживается в данной версии"); // касса Атол
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
            throw new Error("Запрос отчетов по номерам смен пока не реализован!");
        }
    }

    // Action зона

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
        posController.initPOS(this); // передаем необходимые параметры
        stage.setScene(new Scene(root, 530, 540));
        stage.setTitle("Редактирование настройки обмена текущей кассы");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
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

            //удаление старого отчета, если он есть
            fileDelete(getSelectedPOSData()[1] + "\\" + getSelectedPOSData()[3]);
            if (timerClock < Sync1C.REPWAITTIME) throw new Error("Дождитесь окончания предидущего запроса!");
            //проверка на корректность перед запросом
            repRequestControl();
            //формирование файла запроса
            SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            System.out.println(newDateFormat.format(oldDateFormat.parse(dateFrom.getValue().toString())));
            repFileRequest(newDateFormat.format(oldDateFormat.parse(dateFrom.getValue().toString())), newDateFormat.format(oldDateFormat.parse(dateTo.getValue().toString())), getSelectedPOSData()[1] + "\\" + getSelectedPOSData()[4]);
            // ожидание загрузки отчета
            labelRep.setVisible(true);
            labelRep.setTextFill(Color.BLACK);
            labelRep.setText("Ожидаем получения отчета за выбранный период");
            repWait(getSelectedPOSData()[1] + "\\" + getSelectedPOSData()[3], getSelectedPOSData()[1] + "\\" + getSelectedPOSData()[4]); // запуск ожидания отчета

        } catch (Throwable error) {
            labelRep.setTextFill(Color.RED);
            labelRep.setText(error.getMessage());
            // завершение потока
        }
        System.out.println("Кнопка отжата");
    }

    /* внутренний класс (inner class) предназначеннй для ожидания отчета с кассы
        - этот класс имеет интерфейс Runnable, запускается в отдельном потоке
        - он внутренний так как оперирует с объектами внешнего класса (выплевывает текстовые сообщения в элемент формы MainController)
    */
    class RepReq implements Runnable {
        private String repFileName;
        private String repFlagName;

        // конструктор класса
        RepReq(String rFileN, String rFlagN) {
            repFileName = rFileN;
            repFlagName = rFlagN;
        }

        // метод ожидающий отчет (запущен в отдельном процессе, в отдельном классе. Поскольку класс RepReq является inner (внутренним) мы имеем доступ ко всему во внешнем классе, только так как это другой подок, пишем в него через runlater
        public void run() {
            //https://stackoverflow.com/questions/17850191/why-am-i-getting-java-lang-illegalstateexception-not-on-fx-application-thread
            //The user interface cannot be directly updated from a non-application thread. Instead, use Platform.runLater(), with the logic inside the Runnable object.
            for (timerClock = Sync1C.REPWAITTIME; timerClock > 0; --timerClock)
                if (fileExist(repFileName)) { // ура, отчет на диске присутствует
                    runLater( // надо сказать пользователю про это
                            () -> {
                                labelRep.setTextFill(Color.GREEN);
                                labelRep.setText("Отчет получен");
                            });
                    timerClock = Sync1C.REPWAITTIME;
                    fileDelete(repFlagName);
                    break;
                } else try { // отчета нет, надо заснуть на секунду...
                    Thread.sleep(1000l);
                    Platform.runLater(() -> {
                        labelRep.setTextFill(Color.BLACK);
                        labelRep.setText("Ожидаем отчет с кассы " + timerClock);
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            if (timerClock > 0) return;
            else {
                runLater(() -> {
                    labelRep.setTextFill(Color.RED);
                    labelRep.setText("Отчет не получен");
                    timerClock = Sync1C.REPWAITTIME; // важно сбросить таймер, по этому таймеру основной процесс судит о завершенности начатого процесса обмена
                    fileDelete(repFlagName); // удаляем файл-флаг запроса отчета (согласно интсрукции производителя ПО xPOS)
                });
            }
        }
    }

    // проверка существоания файла по заданному пути
    public boolean fileExist(String fileName) {
        //формирование имени файла
        System.out.println("Проверка существования файла " + fileName);
        File f = new File(fileName);
        if (f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    // удаление заданного файла
    public void fileDelete(String fileName) {
        //формирование имени файла
        System.out.println("Удаление файла " + fileName);
        File f = new File(fileName);
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
    }

    //формирование файла-флага (файла запроса отчета)
    private void repFileRequest(String dateF, String dateT, String flagName) throws IOException {
        FileWriter fileWriter;
        fileWriter = new FileWriter(flagName);
        fileWriter.write("$$$TRANSACTIONSBYDATERANGE");
        fileWriter.write("\r\n");
        fileWriter.write(dateF + ";" + dateT);
        fileWriter.write("\r\n");
        fileWriter.close();
    }


    //Обработка нажатия кнопки "Выгрузка отчета в 1С"
    public void repTo1CButton(ActionEvent event) {
        String repNotify = null;
        // Удобно обрабатывать возможные ошибки через блок try/catch пихая в соответствующее место на экране результат выполнения операции
        try {
            if (!(getSelectedPOSData()[5].equals("1")))
                throw new Error("У этой кассы не настроен раздельный учет ИП и ООО. Выгрузка в 1С не нужна.");//проверить что операция парсинга требуется
            if (!fileExist(getSelectedPOSData()[1] + "\\" + getSelectedPOSData()[3]))
                throw new Error("Нет отчета с кассы, нажми кнопку запрос отчета на POS");//проверить, что есть исходный файл отчета
            if (!getSelectedPOSData()[2].equals(Sync1C.ATOLPOS))
                throw new Error("Работа с кассами " + getSelectedPOSData()[2] + " не поддерживается в данной версии"); // касса Атол
            repNotify = RepParser.repParse(getSelectedPOSData()[1] + File.separator + getSelectedPOSData()[3], getSelectedPOSData()[6], getSelectedPOSData()[7]);// print отчета с кассы
        } catch (Error error) {
            labelRep.setTextFill(Color.RED);
            labelRep.setText(error.getMessage());
        } catch (IOException e) {
            labelRep.setTextFill(Color.RED);
            labelRep.setText(e.getMessage());
        } finally {
            if (repNotify != null) {
                labelRep.setTextFill(Color.GREEN);
                labelRep.setText(repNotify);
            }
        }
    }

    //обработка нажатия кнопки "Выгрузка товара на POS"
    public void goodsToPOSButton(ActionEvent event) {
        String notify = null;
        try {
            if (!(getSelectedPOSData()[5].equals("1")))
                throw new Error("У этой кассы не настроен раздельный учет ИП и ООО. Выгрузка на POS не нужна.");// операция парсинга требуется
            if (!fileExist(getSelectedPOSData()[6] + File.separator + Sync1C.GOODS_IP_FILENAME))
                throw new Error("Отсутствует файл с товарами по ИП");// есть флаг файла товаров по ИП
            if (!fileExist(getSelectedPOSData()[6] + File.separator + Sync1C.GOODS_IPFLAG_FILENAME))
                throw new Error("Отсутствует файл-флаг товаров по ИП");// есть флаг файла товаров по ИП
            if (!fileExist(getSelectedPOSData()[7] + File.separator + Sync1C.GOODS_OOOFLAG_FILENAME))
                throw new Error("Отсутствует файл с товарами по ООО");//есть флаг файла товаров по ООО
            if (!fileExist(getSelectedPOSData()[7] + File.separator + Sync1C.GOODS_OOO_FILENAME))
                throw new Error("Отсутствует файл-флаг товаров по ООО");//есть флаг файла товаров по ООО
            if (!getSelectedPOSData()[2].equals(Sync1C.ATOLPOS))
                throw new Error("Работа с кассами " + getSelectedPOSData()[2] + " не поддерживается в данной версии"); // касса Атол
            notify = RepParser.goodsToPOS(getSelectedPOSData()[1], getSelectedPOSData()[6], getSelectedPOSData()[7]);
        } catch (Error error) {
            labelRep.setTextFill(Color.RED);
            labelRep.setText(error.getMessage());
        } catch (IOException e) {
            labelRep.setTextFill(Color.RED);
            labelRep.setText(e.getMessage());
        } finally {
            if (notify != null) {
                labelRep.setTextFill(Color.GREEN);
                labelRep.setText(notify);
            }
        }
    }

}
