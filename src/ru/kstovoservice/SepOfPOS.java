package ru.kstovoservice;

import javax.xml.parsers.ParserConfigurationException;

// типа делаю всё по науке, это интерфейс взаимодействия контроллера для чтения данных,
// конкретная реализация этого интерфейса есть в классе Model (посредством xml)
// но можно ведь сделать и по-другому, а интерфейс типа останется этот же

interface SetOfPOS {

    void addPOS(String posName, String... data);

    void addNewPOS();

    // получить данные POS по названию POS
    String[] getKV(String k);

    // Полный список POS как массив String[]
    String[] getListPOS();

    // удаляет POS из карты POS
    void removePOS(String pos);

    // записывает все POS в файл
    void saveAllDataToFile() throws ParserConfigurationException;
}