package ru.kstovoservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;


public class Lic {

    private static Socket socket;

    // инициализация нового объекта класса Lic
    Lic() {
        System.out.println("Запускаем модуль Lic...");
    }

    // проверка стутуса лицензии
    boolean checkLic() {
        // Пытаемся получить лицензию с сервера ...;
        newLic();
        System.out.println("Проверяем имеющуюся лицензию на корректность ...");
        if (licHash()) {
            System.out.println("Лицензия корректна ...");
            return true;
        } else {
            System.out.println("Лицензия не корректна ...");
        }
        return false;
    }

    boolean licHash() {

        return true;
    }

    // возвращает String с инфой о компе
    String sysInfoPOS() {
        String sysInfo = "";
        // возвращает List<String> с инфой о компе
        class Scr {

            String line, serial = "";

            List<String> strSysInfoPOS = new LinkedList<String>();

            String[] commands;

            Scr(String... strings) {
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
                        System.out.println("Ошибка чтения данных о компе-лицензиате");
                    }
                }
                return strSysInfoPOS;
            }
        }
        for (String s : new Scr("wmic diskdrive get serialnumber", "systeminfo").scrList()) {
            sysInfo = sysInfo + "\n" + s;
        }

        return sysInfo;

    }


    private static Socket createSocket() {
        socket = null;
        System.out.println("Стучимся на сервер лицензий");
        try {
            // У клиента создаем сокет к серверу, указывая адрес сервера лицензий и порт сервера лицензий
            socket = new Socket(Sync1C.HOST_FOR_CLIENT, Sync1C.PORT);
            System.out.println("Сервер ответил ...");
            return socket;
        } catch (Exception e) {
            System.out.println("Сервер не отвечает...");
        }
        return socket;
    }

    // пытаемся получить лицензию с сервера
    // соединяемся с сервером,  получаем ответ и пишем его в файл по возможности
    public void newLic() {

        if (createSocket() != null) {
            System.out.println("Передаем данные претендента...");
            try {

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                String line = sysInfoPOS();
                out.write(line.getBytes());
                out.flush();

                byte[] data = new byte[32 * 1024];
                System.out.println("Ждём ответ сервера ...");
                int readbytes = in.read(data); // блокирующий вызов. тут всё остановится, пока сервер что-то не вернет
                String anser = new String(data, 0, readbytes);
                //System.out.printf("Server> %s \n", anser); //printf - удобная херь со времен С для печати текста по формату
                System.out.println("Ответ получен ...");
                // надыть записать ответ в файл
                fileWrite(anser);


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

    void fileWrite(String anser) {
        System.out.println("Пишем ответ в файл ...");
        System.out.println("Записали ответ в файл ...");
    } // пишет в файл ответ сервера

}
