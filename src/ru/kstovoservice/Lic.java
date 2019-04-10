package ru.kstovoservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ResourceBundle;

import java.net.URL;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Lic { // эта хрень нужна для инициализации нового потока

    private Socket socket;

    // инициализация нового объекта класса Lic
    Lic() {
    }

    // проверка стутуса лицензии
    boolean checkLic() {
        return true;
    }

}
