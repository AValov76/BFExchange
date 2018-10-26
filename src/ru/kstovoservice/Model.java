package ru.kstovoservice;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;



class POSDataModel {

    // собственно тут и хранится xml-запись
    String[] pos = new String[5]; //posName 0, pathPOS 1, typeofPOS 2, repName 3, flagName 4
    final static String[] POS = {"Касса №1", "C:\\Obmen", "Атол", "report.rep", "report.flg"};

    POSDataModel () {
        init();
    }

    POSDataModel (String[] set) {
        for (int i = 0; i < 5; i++) {
            pos[i] = set[i];
        }
    }

    public String getPosName () {
        return pos[0];
    }

    public String getPathPOS () {
        return pos[1];
    }

    public String getTypeofPOS () {
        return pos[2];
    }

    public String getRepName () {
        return pos[3];
    }

    public String getFlagName () {
        return pos[4];
    }

    public String[] getPos () {
        return pos;
    }

    public void setPos (String posName, String pathPOS, String typeofPOS, String repName, String flagName) {
        pos[0] = posName;
        pos[1] = pathPOS;
        pos[2] = typeofPOS;
        pos[3] = repName;
        pos[4] = flagName;
    }

    @Override
    public String toString () {
        String string = new String();
        for (String s : pos
                ) {
            string += s + " ";
        }
        return string;
    }

    public void init () {
        pos = POS;
    }
}

public class Model {
    private static final String FILENAME = "company.xml";

    public Model () {
        // проверка на существование файла и если его нет или оно неправильное- создание файла по умолчанию
        posinit();
    }

    private void addPOS (POSDataModel newpos, Document document, Element company) {

        // Элемент типа pos
        Element posName = document.createElement("posName");
        company.appendChild(posName);

        // Еще можно сделать так
        posName.setAttribute("posName", newpos.getPosName());

        // Определяем posName
        Element pathPOS = document.createElement("pathPOS");
        pathPOS.setTextContent(newpos.getPathPOS());
        posName.appendChild(pathPOS);

        // Определяем typeofPOS
        Element typeofPOS = document.createElement("typeofPOS");
        typeofPOS.setTextContent(newpos.getTypeofPOS());
        posName.appendChild(typeofPOS);

        // Определяем repName
        Element repName = document.createElement("repName");
        repName.setTextContent(newpos.getRepName());
        posName.appendChild(repName);

        // Определяем flagName
        Element flagName = document.createElement("flagName");
        flagName.setTextContent(newpos.getFlagName());
        posName.appendChild(flagName);

    }
    private void saveData(Document document) throws ParserConfigurationException {

        try{
        // Сохранить текстовое представление XML документа в файл
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(
                new File(System.getProperty("user.dir")
                        + File.separator + FILENAME));

        // Для соображений отладки можно вывести результат работы
        // программы на стандартный вывод
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);

        System.out.println("Документ сохранен!");

    } catch (TransformerException ex) {
        Logger.getLogger(Model.class.getName())
                .log(Level.SEVERE, null, ex);
    }
}
    void posinit () {
        // готовим данные по умолчанию для создания исходного файла
        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            // Корневой элемент
            Element company = document.createElement("company");
            document.appendChild(company);
            // добавляем 2 новых строки в документ
            addPOS(new POSDataModel(), document,company);
            addPOS(new POSDataModel(), document,company);
            // сохраняем документ
            saveData(document);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Model.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
