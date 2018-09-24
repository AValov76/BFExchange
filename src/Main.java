import javafx.stage.Modality;
import ru.kstovoservice.test.Test;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public void start (final Stage primaryStage) {

        Button button = new Button("О программе");
        //button.setText("О программе");

        button.setOnAction(new EventHandler<ActionEvent>() {

            public void handle (ActionEvent event) {

                Label label = new Label("(c) ООО \"Кстовоторгсервис\". Версия 1.0. \n Тел. +7 (83145) 9-06-98 \n г.Кстово, 2018");

                StackPane secondaryLayout = new StackPane();
                secondaryLayout.getChildren().add(label);

                Scene secondScene = new Scene(secondaryLayout, 230, 130);

                // New window (Stage)
                Stage newWindow = new Stage();
                newWindow.setTitle("О программе");
                newWindow.setScene(secondScene);
                newWindow.initModality(Modality.APPLICATION_MODAL);
                newWindow.setResizable(false);
                newWindow.setWidth(300);
                newWindow.setMaxHeight(80);
                // Set position of second window, related to primary window.
                newWindow.setX(primaryStage.getX() + 200);
                newWindow.setY(primaryStage.getY() + 100);

                newWindow.show();
            }
        });
        StackPane root = new StackPane();
        root.getChildren().add(button);

        Scene scene = new Scene(root, 450, 250);

        primaryStage.setTitle("Front-Back обмен v 1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main (String... args) {
        //System.out.println(new Test());
        launch(args);
    }
}
