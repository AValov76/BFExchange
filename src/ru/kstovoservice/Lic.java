package ru.kstovoservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

public class Lic { // эта хрень нужна для инициализации нового потока

    public static final int PORT = 13539; // порт на котором сидит сервер лицензий
    public static final String HOST_FOR_CLIENT = "82.208.70.88";//
    //    public static final String HOST_FOR_CLIENT = "localhost";//
    private static Socket socket;

    // инициализация нового объекта класса Lic
    Lic() {

        for (String s : sysInfoPOS()
        ) {
            System.out.println(s);
        }

    }

    // проверка стутуса лицензии
    boolean checkLic() {
        return true;
    }

    // возвращает уникальный слепок характеристик данного компьютера в формате списка строк
    List<String> sysInfoPOS() {
        // innerclass так как будет не один запрос а несколько
        class Scr {
            String line, serial = "";
            List<String> strSysInfoPOS;
            String[] commands;

            Scr(String... strings) {
                strSysInfoPOS = new LinkedList<String>();
                commands = strings;
            }

            public List<String> scrList() {

                for (String s : commands
                ) {
                    try {
                        Process process = Runtime.getRuntime().exec("cmd /c " + s);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                        while ((line = in.readLine()) != null) {
                            strSysInfoPOS.add(line);
                        }
                        in.close();
                    } catch (Exception e) {
                    }
                }
                return strSysInfoPOS;
            }
        }

        return new Scr("wmic diskdrive get serialnumber", "systeminfo" ).scrList();

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
