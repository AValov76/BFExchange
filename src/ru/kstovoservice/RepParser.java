package ru.kstovoservice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;


public class RepParser {


    public static final Map<String, String> DT; // возможные типы транзакций (сделано для справки) - не пользовался

    static {
        DT = new TreeMap<String, String>();
        DT.put("1", "Регистрация товара по свободной цене");
        DT.put("2", "Сторно товара по свободной цене");
        DT.put("3", "Установка спеццены");
        DT.put("4", "Налог на товар по свободной цене");
        DT.put("6", "Регистрация товара в ККТ под новый порядок по свободной цене");
        DT.put("11", "Регистрация товара из справочника");
        DT.put("12", "Сторно товара из справочника");
        DT.put("13", "Установка цены из прайс-листа");
        DT.put("14", "Налог на товар из справочника");
        DT.put("15", "Скидка суммой на позицию товара");
        DT.put("16", "Регистрация товара в ККТ под новый порядок из справочника");
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
    }// static блок будет выполнен при инициализации класса


    //парсер отчета с кассы - основной модуль
    public static String repParse (String repfileName, String repIPPath, String repOOOPath) throws IOException {

        List<String> repXPOS = Files.readAllLines(Paths.get(repfileName), Charset.forName("windows-1251")); //пихаем файл построчно в коллекцию строк
        List<String> repIP = new ArrayList<String>(); // отчет ИП
        List<String> repOOO = new ArrayList<String>(); // отчет ООО
        List<String> deltaRepIP = new ArrayList<String>(); // чек ИП
        List<String> deltaRepOOO = new ArrayList<String>(); // чек ООО

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
                    s[16] = Sync1C.IP_PRINTGROUP_CODE;
                    s[7] = s[7].substring(1); // удаляем первый символ из строки SKU
                    deltaRepOOO.add(strСompile(s)); //
                    break;
                // Отправка в ЕГАИС
                case "EGAIS":
                    s = strDecompile(line);
                    s[16] = Sync1C.IP_PRINTGROUP_CODE;
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
        rep_IP_OOO(repIPPath, repOOOPath, new List[]{repIP, repOOO});

        // удаляем исходный отчет, на основании которого делались отчеты по ИП и ООО
        filedelete(repfileName);

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
                case "14":
                    if (strDecomp[16].equals(Sync1C.OOO_PRINTGROUP_CODE)) {
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
                    if (strDecomp[16].equals(Sync1C.OOO_PRINTGROUP_CODE)) return "EGAIS";
                    return "ITEM_IP";
            }
        }
        return "00";
    }

    //разбивка строки на массив строк
    private static String[] strDecompile (String string) {
        Pattern p = Pattern.compile(";");
        String[] str = p.split(string);
        // но оно не сохранило последние пустые части -->   ;;; ибо так работает split
        //посчитаем, сколько их было всего в строке
        int semicolonCount;
        semicolonCount = string.length() - string.replace(";", "").length();
        // вычтем те, которые учлись
        semicolonCount = (semicolonCount - str.length) + 1;
// допишем точку с запятой в последний элемент массива
        for(int i=semicolonCount;i>0;i--) str[str.length - 1] = str[str.length - 1] + ";";
        return str;
    }

    //сливка массива строк в 1 строку
    private static String strСompile (String[] s) {
        String str = String.join(";", s);
        return str;
    }


    private static void rep_IP_OOO (String repIPPath, String repOOOPath, List... lists) throws IOException {
        String pathIP = repIPPath + File.separator + Sync1C.REP_IP_FILENAME;
        String pathOOO = repOOOPath + File.separator + Sync1C.REP_OOO_FILENAME;
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

    // слив товаров по ИП ООО в 1 файл...
    public static String goodsToPOS (String goodsPOSPath, String goodsIPPath, String goodsOOOPath) throws IOException {

        String fullPathGoodsPOS = goodsPOSPath + File.separator + Sync1C.GOODS_POS_FILENAME;
        String fullPathGoodsFlagPOS = goodsPOSPath + File.separator + Sync1C.GOODS_POSFLAG_FILENAME;
        String fullPathGoodsIP = goodsIPPath + File.separator + Sync1C.GOODS_IP_FILENAME;
        String fullPathGoodsIPFlag = goodsIPPath + File.separator + Sync1C.GOODS_IPFLAG_FILENAME;
        String fullPathGoodsOOO = goodsOOOPath + File.separator + Sync1C.GOODS_OOO_FILENAME;
        String fullPathGoodsFlagOOO = goodsOOOPath + File.separator + Sync1C.GOODS_OOOFLAG_FILENAME;

        //удаляем файл goodsPOS если он вообще есть и его флаг
        filedelete(fullPathGoodsPOS, fullPathGoodsFlagPOS);

        // Удалили, ок. Таперича готовим место, куда писать
        FileWriter goodsFile;
        goodsFile = new FileWriter(fullPathGoodsPOS);
        // данные из файла товаров ИП непосредственно перекатываем в goodsPOS
        List<String> goods = Files.readAllLines(Paths.get(fullPathGoodsIP), Charset.forName("windows-1251")); //пихаем файл построчно в коллекцию строк
        for (String string : goods
                ) {
            goodsFile.write(string);
            goodsFile.write("\r\n");
        }

        //данные из файла товаров ООО модифицируем и дописываем к файлу goodsPOS
        goods = Files.readAllLines(Paths.get(fullPathGoodsOOO), Charset.forName("windows-1251"));
        for (String string : goods
                )
            if (string.length() > 50) { //пихаем только сроки за исключением шапки
                String[] str = strDecompile(string); // надо дописать девятку к SKU и 39 поле (группа печати) заменить 1 на 2
                str[0] = Sync1C.SKU_MOD + str[0];
                str[38] = Sync1C.OOO_PRINTGROUP_CODE;
                string = strСompile(str);
                goodsFile.write(string);
                goodsFile.write("\r\n");
            }

        //удаляем файлы goodsIP goodsOOO как обработанные и их флаги (по руководству интератора  надо поменять первый символ и удалить только файл-флаг, но мне хочется именно удалить всё)
        filedelete(fullPathGoodsIP, fullPathGoodsIPFlag, fullPathGoodsOOO, fullPathGoodsFlagOOO);

        // Также надо сделать файл-флаг для goodsPOS
        FileWriter fileWritergoodsPOSFlag;
        fileWritergoodsPOSFlag = new FileWriter(fullPathGoodsFlagPOS);
        fileWritergoodsPOSFlag.close();

        //ну вот и всё
        goodsFile.close();
        return "Выгрузка товара на кассу произведена";
    }

    // хрень для удаления файла
    public static void filedelete (String... fullfilename) {
        for (String str : fullfilename
                ) {
            File file = new File(str);
            if (file.exists() && !file.isDirectory()) {
                file.delete();
            }
        }
    }


}
