package ru.kstovoservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kstovoservice.MainController;
//import ru.kstovoservice.MainController;

public class Sync1C extends Application {

    // константы
    public static final String GOODS_IP_FILENAME="goodsIP.spr";
    public static final String GOODS_IPFLAG_FILENAME="goodsIP.flg";
    public static final String GOODS_OOO_FILENAME="goodsOOO.spr";
    public static final String GOODS_OOOFLAG_FILENAME="goodsOOO.flg";
    public static final String GOODS_POS_FILENAME="goods.spr";
    public static final String GOODS_POSFLAG_FILENAME="goods.flg";
    public static final String REP_POS_FILENAME="report.rep";
    public static final String REP_IP_FILENAME="reportIP.rep";
    public static final String REP_OOO_FILENAME="reportOOO.rep";
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
    public void start (Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController(); //контроллер главной формы
        mainController.mainController = mainController;
        primaryStage.setTitle("Синхронизатор отчетов");
        primaryStage.setScene(new Scene(root, 550, 460));
        primaryStage.show();

    }
    
    public static void main (String[] args) {
        launch(args);
    }

}
