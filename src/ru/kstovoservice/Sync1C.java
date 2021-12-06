package ru.kstovoservice;


/*
Порядок формирования лицензии на программу
1. Узнать SN компа командой wmic diskdrive get serialnumber
C:\Users\Пользователь>
2. Скопировать номер в строку public static final String licSerialNumber = "сюда";
3. Установить требуемую дату лицензии
4. Скомпилировать проект (он скомпилируется в папк test)
5. шелкнуть по C:\test\exe_builder.exe4j
6. нажать в открывшемся по кнопку finish
7. положить exe шник с xml в папку на комп клиенту

C:\Users\Пользователь>
 */
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sync1C extends Application {
    /*
    0000_0000_0100_0000_4CE0_0018_DD8C_9084. - ЭОЛ Дубрава
     */
    public static final String licSerialNumber = "0000_0000_0100_0000_4CE0_0018_DD8C_9084."; //мой 56242F970E7B3589 или S2BENWAJ717128K // клиента 2H0820074970
    public static final String LIC_DATE = "2022-05-20";
    public static Lic lic;
    static {
        try {
            Date licDate = new SimpleDateFormat("yyyy-MM-dd").parse(LIC_DATE);
            lic = new Lic(licDate,licSerialNumber);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // константы, да дохуя констант получилось...
    public static final int PORT = 13539; // порт на котором сидит сервер лицензий
    public static final String HOST_FOR_CLIENT = "127.0.0.1";//
    //    public static final String HOST_FOR_CLIENT = "82.208.70.88";//
    //    public static final String HOST_FOR_CLIENT = "localhost";//
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
    public static final int REPWAITTIME = 120; // время ожидания отчета в секундах
    public static final String PREFFILENAME = "company.xml";
    public static final String POSNAME = "Касса №";
    public static final String SHTRIHPOS = "Штрих";
    public static final String ATOLPOS = "Атол";
    public static final String[] POSDATANAME = {"pathPOS", "typeofPOS", "repName", "flagName", "", "checkBoxIPOOO", "pathIP", "pathOOO"}; // пока не использовал
    public static final String[] POSDATA = {"C:\\Obmen", ATOLPOS, REP_POS_FILENAME, "report.flg", "0", "C:\\Obmen", "C:\\Obmen"};

    public Sync1C() throws ParseException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception { // неплохо про устройство Stage-->Scence-->Parent-->None написано тут https://metanit.com/java/javafx/1.5.php

        //в зависимости от результата проверки наличия лицензии ...
        if (lic.checkLic()) {
            System.out.println("Запускаем приложение ...");
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
            alert.setContentText("Обратитесь к разработчику! \nВалов Андрей Викторович тел.\n+7(902)306-47-96 ");
            alert.showAndWait();
        }

    }

    public static void main(String[] args) {
        // проверка лицензии на продукт
        Application.launch(args);
    }

}
