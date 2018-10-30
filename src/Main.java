
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kstovoservice.MainController;

public class Main extends Application {
    @Override
    public void start (Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ru/kstovoservice/Main.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController(); //контроллер главной формы
        mainController.mainController = mainController;
        primaryStage.setTitle("Синхронизатор отчетов");
        primaryStage.setScene(new Scene(root, 550, 440));
        primaryStage.show();

    }
    
    public static void main (String[] args) {
        launch(args);
    }

}
