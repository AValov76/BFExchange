package ru.kstovoservice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class RepParser {


    public static final Set<String> START_CHECK; // Открытие/закрытие документа
    public static final Set<String> REGITEM; // Регистрациz товара
    public static final Set<String> REGCASH; // Регистрация купюр
    public static final Set<String> PAY; // Оплата
    public static final Set<String> DISCOUNTP; // Скидки на позицию
    public static final Set<String> DISCOUNTD; // Скидки на документ
    public static final Set<String> ETSEP; // Дополнительные
    public static final Set<String> REP; // Отчеты
    public static final Set<String> EGAIS;//Отправка данных в ЕГАИС


    static {
        START_CHECK = new TreeSet<String>();
        START_CHECK.add("42"); // Открытие документа
        START_CHECK.add("55"); // Закрытие документа
        START_CHECK.add("56"); // Документ не закрыт в ККМ
        START_CHECK.add("45"); // Закрытие документа в ККМ
        START_CHECK.add("49"); // Закрытие документа по ГП

        REGITEM = new TreeSet<String>();
        REGITEM.add("1");// Регистрация товара по свободной цене
        REGITEM.add("11");// Регистрация товара из справочника
        REGITEM.add("2");// Сторно товара по свободной цене
        REGITEM.add("12");// Сторно товара из справочника

        REGCASH = new TreeSet<String>();
        REGCASH.add("21");// Регистрация купюр по свободной цене
        REGCASH.add("23");// Регистрация купюр из справочника
        REGCASH.add("22");// Сторно купюр по свободной цене
        REGCASH.add("24");// Сторно купюр из справочника

        PAY = new TreeSet<String>();
        PAY.add("40");// Оплата с вводом суммы клиента
        PAY.add("41");// Оплата без ввода суммы клиента
        PAY.add("43");// Распределение оплаты по ГП

        //
        DISCOUNTP = new TreeSet<String>();
        DISCOUNTP.add("15");// Скидка суммой на позицию товара
        DISCOUNTP.add("17");// Скидка % на позицию товара

        //Скидки на документ
        DISCOUNTD = new TreeSet<String>();
        DISCOUNTD.add("35");// Скидка суммой на документ
        DISCOUNTD.add("37");// Скидка % на документ
        DISCOUNTD.add("85");// Скидка суммой на документ, распределенная по позициям
        DISCOUNTD.add("87");// Скидка % на документ, распределенная по позициям

        //Дополнительные
        ETSEP = new TreeSet<String>();
        ETSEP.add("50");// Внесение
        ETSEP.add("51");// Выплата

        // Отчеты
        REP = new TreeSet<String>();
        REP.add("60"); // Отчет без гашения
        REP.add("63"); // Отчет с гашением
        REP.add("64"); // Документ открытия смены
        REP.add("61"); // Закрытие смены
        REP.add("62"); // Открытие смены

        // Отправка данных в ЕГАИС
        EGAIS = new TreeSet<String>();
        EGAIS.add("120");// Отправка данных в ЕГАИС

    }


    //парсер отчета с кассы - основной модуль
    public static String repParse(String fileName) throws IOException {

        List<String> repXPOS = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8); //пихаем файл построчно в коллекцию строк
        List<String> repIP = new ArrayList<String>(); // отчет для ИП
        List<String> repOOO = new ArrayList<String>(); // отчет для ООО
        Set<String> startCheck =  new TreeSet<String>(); //набор комбинаций старта документа
        int i = 0;
        for (String line : repXPOS) {
            switch (repStringAnalize(line)) {
                case 1: // если строка предназначена для IP

                    break;
                case 2: // если строка предназначена для OOO

                    break;
                case 3: // если строка должна быть и там и там
                    repIP.add(line.toString());
                    repOOO.add(line.toString());
                    break;
            }

            //System.out.println("Новая строка номер"+i+++" "+line);
        }

        return "";
    }

    //проверяет очередную строку из отчета и возвращает ответ, куда ее направлять 1 - ИП 2 - ООО 3- и туда и туда
    private static int repStringAnalize(String repStr) {
        if (repStr.length() < 5) { // проверка шапки отчета
            return 3;
        } else { strDecomile(repStr);
        }
        return 1;
    }

    //разбивка строки на массив строк
    private static String[] strDecomile(String string) {
        Pattern p = Pattern.compile(";");
        String[] str = p.split(string);
        System.out.println(str[3]);
        return str;
    }
}
