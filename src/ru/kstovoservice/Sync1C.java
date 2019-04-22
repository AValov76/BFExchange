package ru.kstovoservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.scene.Group;

public class Sync1C extends Application {

    public static Lic lic = new Lic();

    // константы, да дохуя констант получилось...
    public static final String GOODS_IP_FILENAME = "goodsIP.spr";
    public static final String GOODS_IPFLAG_FILENAME = "goodsIP.flg";
    public static final String GOODS_OOO_FILENAME = "goodsOOO.spr";
    public static final String GOODS_OOOFLAG_FILENAME = "goodsOOO.flg";
    public static final String GOODS_POS_FILENAME = "goods.spr";
    public static final String GOODS_POSFLAG_FILENAME = "goods.flg";
    public static final String REP_POS_FILENAME = "report.rep";
    public static final String REP_IP_FILENAME = "reportIP.rep";
    public static final String REP_OOO_FILENAME = "reportOOO.rep";
    public static final String SKU_MOD = "9";
    public static final String IP_PRINTGROUP_CODE = "1";
    public static final String OOO_PRINTGROUP_CODE = "2";
    public static final int REPWAITTIME = 15;
    public static final String PREFFILENAME = "company.xml";
    public static final String POSNAME = "Касса №";
    public static final String SHTRIHPOS = "Штрих";
    public static final String ATOLPOS = "Атол";
    public static final String[] POSDATANAME = {"pathPOS", "typeofPOS", "repName", "flagName", "", "checkBoxIPOOO", "pathIP", "pathOOO"}; // пока не использовал
    public static final String[] POSDATA = {"C:\\Obmen", ATOLPOS, REP_POS_FILENAME, "report.flg", "0", "C:\\Obmen", "C:\\Obmen"};

    @Override
    public void start(Stage primaryStage) throws Exception { // неплохо про устройство Stage-->Scence-->Parent-->None написано тут https://metanit.com/java/javafx/1.5.php

        //в зависимости от результата проверки наличия лицензии ...
        if (lic.checkLic()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();
            MainController mc = loader.getController(); //контроллер главной формы
            primaryStage.setTitle("Синхронизатор отчетов");
            primaryStage.setScene(new Scene(root, 550, 460));
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    mc.programExit();
                }
            });
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Информационное сообщение");
            alert.setHeaderText("Отсутствует лицензия на программный продукт!");
            alert.setContentText("Обратитесь к разработчику! \nООО Кстовоторгсервис тел.\n+7(902)306-47-96 ");
            alert.showAndWait();
        }

    }

    public static void main(String[] args) {
        // старт запроса сервера
        //lic.doJOB();
        // проверка лицензии на продукт
        Application.launch(args);
    }

}
