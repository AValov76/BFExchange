import ru.kstovotorgservice.Controller;
import ru.kstovotorgservice.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Stage window;
    @Override
    public void start (Stage primaryStage) throws Exception {
        window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("ru/kstovotorgservice/MainScene.fxml"));
        primaryStage.setTitle("Синхронизатор отчетов");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }


    public static void main (String[] args) {
        launch(args);
    }

}
