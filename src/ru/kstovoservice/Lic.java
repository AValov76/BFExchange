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
import sun.nio.ch.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;

import java.net.URL;
import java.util.ResourceBundle;

public class Lic { // эта хрень нужна для инициализации нового потока

    public static final int PORT = 13539; // прописан проброс порта на 82.208.70.88 на комп
    public static final String HOST_FOR_CLIENT = "82.208.70.88";//
    //    public static final String HOST_FOR_CLIENT = "localhost";//
    private static Socket socket;

    // инициализация нового объекта класса Lic
    Lic() {

    }

    // проверка стутуса лицензии
    boolean checkLic() {
        // вставлю ка я сюды запуск сокета
        return true;
    }


    public static Socket createSocket() {
        socket = null;
        System.out.println("тут ...");
        try {
            // У клиента создаем сокет к серверу, указывая адрес сервера лицензий и порт сервера лицензий
            socket = new Socket(HOST_FOR_CLIENT, PORT);
            System.out.println("создаем сокет ...");
            return socket;
        } catch (Exception e) {
            System.out.println("Сервер не отвечает...");
        }
        return socket;
    }

    public void doJOB() {

        if (createSocket() != null) {
            System.out.println("сокет создался...");
            try {

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                String line = "Hello!";
                out.write(line.getBytes());
                out.flush();

                byte[] data = new byte[32 * 1024];
                int readbytes = in.read(data); // блокирующий вызов. тут система остановится, пока сервер что-то не вернет
                System.out.printf("Server> %s", new String(data, 0, readbytes));
                System.out.println("\n");
            } catch (SocketException e) {
                System.out.println(e);
            } catch (NullPointerException e) {
                System.out.println("Не удалось соединиться с сервером (Socket=null)!");
            } catch (Exception e) {

            } finally {
                // IoUtil.closeQuietly(socket);
            }
        }
    }
}
