package ru.kstovoservice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;


public class RepParser {

    // возможные типы транзакций (сделано для справки) - не пользовался
    public static final Map<String, String> DT;

    static {
        DT = new TreeMap<String, String>();
        DT.put("1", "Регистрация товара по свободной цене");
        DT.put("2", "Сторно товара по свободной цене");
        DT.put("11", "Регистрация товара из справочника");
        DT.put("12", "Сторно товара из справочника");
        DT.put("15", "Скидка суммой на позицию товара");
        DT.put("17", "Скидка % на позицию товара");
        DT.put("21", "Регистрация купюр по свободной цене");
        DT.put("22", "Сторно купюр по свободной цене");
        DT.put("23", "Регистрация купюр из справочника");
        DT.put("24", "Сторно купюр из справочника");
        DT.put("35", "Скидка суммой на документ");
        DT.put("37", "Скидка % на документ");
        DT.put("40", "Оплата с вводом суммы клиента");
        DT.put("41", "Оплата без ввода суммы клиента");
        DT.put("42", "Открытие документа");
        DT.put("43", "Распределение оплаты по ГП");
        DT.put("45", "Закрытие документа в ККМ");
        DT.put("49", "Закрытие документа по ГП");
        DT.put("50", "Внесение");
        DT.put("51", "Выплата");
        DT.put("55", "Закрытие документа");
        DT.put("56", "Документ не закрыт в ККМ");
        DT.put("60", "Отчет без гашения");
        DT.put("61", "Закрытие смены");
        DT.put("62", "Открытие смены");
        DT.put("63", "Отчет с гашением");
        DT.put("64", "Документ открытия смены");
        DT.put("85", "Скидка суммой на документ, распределенная по позициям");
        DT.put("87", "Скидка % на документ, распределенная по позициям");
        DT.put("120", "Отправка данных в ЕГАИС");
    }


    //парсер отчета с кассы - основной модуль
    public static String repParse (String repfileName, String repIPPath, String repOOOPath) throws IOException {

        List<String> repXPOS = Files.readAllLines(Paths.get(repfileName), StandardCharsets.UTF_8); //пихаем файл построчно в коллекцию строк
        List<String> repIP = new ArrayList<String>(); // отчет для ИП
        List<String> repOOO = new ArrayList<String>(); // отчет для ООО
        List<String> deltaRepIP = new ArrayList<String>(); // чек для ИП
        List<String> deltaRepOOO = new ArrayList<String>(); // чек для ООО

        int i = 0;

        for (String line : repXPOS) {
            String[] s = strDecompile(line);
            switch (repStringAnalize(line)) {
                // Старт документа
                case "42":
                    deltaRepIP.add(line);
                    deltaRepOOO.add(line);
                    break;
                // Регистрация товара
                case "ITEM_IP":
                    deltaRepIP.add(line);
                    break;
                case "ITEM_OOO":
                    s[16] = MainController.IP_PRINTGROUP_CODE;
                    s[7] = s[7].substring(1); // удаляем первый символ из строки SKU
                    deltaRepOOO.add(strСompile(s)); //
                    break;
                // Отправка в ЕГАИС
                case "EGAIS":
                    s = strDecompile(line);
                    s[16] = MainController.IP_PRINTGROUP_CODE;
                    deltaRepOOO.add(strСompile(s)); //
                    break;

                case "99": // эти строки точно должны быть и в отчете ИП и в отчете ООО
                    repIP.add(line);
                    repOOO.add(line);
                    break;

                case "CLOSE_DOC":
                    if (deltaRepLength(deltaRepIP)) {
                        deltaRepIP.add(line);
                        repIP.addAll(deltaRepIP);
                    }
                    if (deltaRepLength(deltaRepOOO)) {
                        deltaRepOOO.add(line);
                        repOOO.addAll(deltaRepOOO);

                    }
                    deltaRepIP.clear();
                    deltaRepOOO.clear();
                    break;
            }

        }
        //printIP_OOO(new List[]{repIP, repOOO});
        rep_IP_OOO(repIPPath,repOOOPath,new List[]{repIP, repOOO});
        return "Сформированы отчеты по ИП и ООО за заданный период";
    }

    // проверка длины чека
    static boolean deltaRepLength (List list) {
        return (!(list.size() == 1));
    }

    // Распечатка отчетов по ИП и ООО
    static void printIP_OOO (List... IOLIST) {
        for (List<String> l : IOLIST
                ) {
            System.out.println("-------------------Отчет новой организации-----------------------");
            for (String s : l
                    ) {
                System.out.println(s);
            }
        }
    }

    // предварительная разборка строк
    // функция анализирует строки и для разных типов транзакции назначает порой одну и ту же операцию
    private static String repStringAnalize (String repStr) {

        String[] strDecomp = strDecompile(repStr); // разбираем строку на части

        if (repStr.length() < 5) {
            return "99"; // Шапка отчета должна быть везде
        } else {
            switch (strDecomp[3]) {
                // Старт документа
                case "42":
                    return "42";
                //Регистрация товара
                case "1":
                case "11":
                case "2":
                case "12":
                    if (strDecomp[16].equals(MainController.OOO_PRINTGROUP_CODE)) {
                        return "ITEM_OOO";
                    } else
                        return "ITEM_IP";
                    // Отправка в ЕГАИС
                case "120":
                    return "EGAIS";

                // Закрытие документа
                case "55":
                case "56":
                    return "CLOSE_DOC";
                case "49":
                case "45":
                    if (strDecomp[16].equals(MainController.OOO_PRINTGROUP_CODE)) return "EGAIS";
                    return "ITEM_IP";
            }
        }
        return "00";
    }

    //разбивка строки на массив строк
    private static String[] strDecompile (String string) {
        Pattern p = Pattern.compile(";");
        String[] str = p.split(string);
        return str;
    }

    //сливка массива строк в 1 строку
    private static String strСompile (String[] s) {
        String str = String.join(";", s);
        return str;
    }

    private static void rep_IP_OOO (String repIPPath, String repOOOPath, List... lists) throws IOException {
        String pathIP = repIPPath + "\\" + MainController.REP_IP_FILENAME;
        String pathOOO = repOOOPath + "\\" + MainController.REP_OOO_FILENAME;
        // удаляем то, что есть перед записью
        File repIP = new File(pathIP);
        if (repIP.exists() && !repIP.isDirectory()) {
            repIP.delete();
        }
        File repOOO = new File(pathOOO);
        if (repOOO.exists() && !repOOO.isDirectory()) {
            repOOO.delete();
        }

        //попытка записать на диск файлы
        int i = 0;
        FileWriter fileWriter;
        for (List<String> list : lists) {
            // первый раз пишем в файл с данными по ИП
            if (i++ == 0) {
                fileWriter = new FileWriter(pathIP);
            }
            // второй раз пишем в файл ООО
            else {
                fileWriter = new FileWriter(pathOOO);
            }
            for (String string : list
                    ) {
                fileWriter.write(string);
                fileWriter.write("\r\n");
            }
            fileWriter.close();
        }
    }

}
