package ru.kstovoservice;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


// реализация интерфейса взаимодействия с данными


// ну а это конкретная реализация модели, файловый вариант

public class Model implements SetOfPOS {

    // по сути контейнер для данных из xml - файла. тут хранятся все данные по POSам
    private java.util.Map<String, String[]> mapPOS = new java.util.TreeMap<>();
    private Collection<String> s = new ArrayList();
    // путь к файлу с данными на диске

    // инициализация модели
    public Model() throws ParserConfigurationException {

        // проверка на существование файла с данными
        if (!loadData()) {
            //добавляем хоть что-то к данным (настройки по умолчанию
            addPOS(Sync1C.POSNAME + "1", Sync1C.POSDATA);
            // и сохраняем всё в файл
            saveAllDataToFile();
        }
        //прогрузка данных из существующего или из нового файла
        loadmapPOS();
    }

    // загрузка данных по POS из файла в mapPOS
    private void loadmapPOS() {

        Document document;

        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(System.getProperty("user.dir")
                    + File.separator + Sync1C.PREFFILENAME);
            Node root = document.getDocumentElement(); // корневой узел
            NodeList c = root.getChildNodes(); // список детей корневого узла
            for (int i = 0; i < c.getLength(); i++) {
                String k = new String();
                String[] v = new String[7];
                Node pos = c.item(i); // один из детей из списка с - списка компаний
                k = pos.getAttributes().item(0).getTextContent();
                s.add(k);
                // Если нода не текст, то это книга - заходим внутрь
                NodeList posData = pos.getChildNodes();
                for (int j = 0; j < posData.getLength(); j++) {
                    Node posProp = posData.item(j);
                    //System.out.println(posProp+" 3");
                    // Если нода не текст, то это один из параметров книги - печатаем
                    //System.out.println(posProp.getNodeName() + ":" + posProp.getChildNodes().item(0).getTextContent() + " 3");
                    v[j] = posProp.getChildNodes().item(0).getTextContent();
                    s.add(v[j]);
                }
                addPOS(k, v);
            }
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

    }

    // печать данных всех POS
    public void print(Map<String, String[]> mp) {
        for (java.util.Map.Entry entry : mp.entrySet()) {
            //получить ключ
            String key = (String) entry.getKey();
            //получить значение
            String[] value = (String[]) entry.getValue();
        }

        for (String s : mp.keySet()
        ) {
            System.out.print("Key " + s + ": [");
            for (String v : mp.get(s)
            ) {
                System.out.print(v + ",");
            }
            System.out.print("]");
            System.out.println();
        }
    }

    //проверка на существование файла с данными по POS
    private boolean loadData() {

        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(System.getProperty("user.dir")
                    + File.separator + Sync1C.PREFFILENAME);

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
            return false;
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
            return false;
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            return false;
        }

        return true;
    }


    //реализация интерфейса


    // добавляет строки в структуру документа (для сохранения)

    @Override
    public String[] getListPOS() {
        return mapPOS.keySet().toArray(new String[0]); // оно получает String и возвращает его же
    }

    @Override
    public void addPOS(String k, String[] v) {
        mapPOS.put(k, v);
    }

    @Override
    public void addNewPOS() {
        addPOS(Sync1C.POSNAME + (mapPOS.size() + 1), Sync1C.POSDATA);
    }

    @Override
    public String[] getKV(String k) {
        String[] kv = new String[8];
        kv[0] = k;
        int i = 1;
        for (String s : mapPOS.get(k)
        ) {
            kv[i++] = s;
        }
        return kv;
    }

    @Override
    public void removePOS(String pos) {
        mapPOS.remove(pos);
    }

    // записывает все POS в файл
    @Override
    public void saveAllDataToFile() throws ParserConfigurationException {
        //структура, в которой лежат данные из файла на диске. используется дважды
        // - при инициализации модели, для прогрузки
        Document document;
        // по сути нахер не надо пока - сделано про запас, под конторы типа АВС с 2 организациями
        Element company;

// создание нового файла если такового не было
        document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        company = document.createElement("company");
        document.appendChild(company);
        //print(mapPOS);
// Добавление POS в документ
        for (String pos : mapPOS.keySet()
        ) {
            // Элемент типа pos
            Element posName = document.createElement("POS");
            company.appendChild(posName);
            posName.setAttribute("posName", pos);
            // Определяем pathPOS
            Element pathPOS = document.createElement("pathPOS");
            pathPOS.setTextContent(mapPOS.get(pos)[0]); // pathPOS
            posName.appendChild(pathPOS);
            // Определяем typeofPOS
            Element typeofPOS = document.createElement("typeofPOS");
            typeofPOS.setTextContent(mapPOS.get(pos)[1]);
            posName.appendChild(typeofPOS);
            // Определяем repName
            Element repName = document.createElement("repName");
            repName.setTextContent(mapPOS.get(pos)[2]);
            posName.appendChild(repName);
            // Определяем flagName
            Element flagName = document.createElement("flagName");
            flagName.setTextContent(mapPOS.get(pos)[3]);
            posName.appendChild(flagName);
            // Определяем checkBoxIPOOO
            Element checkBoxIPOOO = document.createElement("checkBoxIPOOO");
            checkBoxIPOOO.setTextContent(mapPOS.get(pos)[4]); // pathPOS
            posName.appendChild(checkBoxIPOOO);
            // Определяем pathPOSIP
            Element pathPOSIP = document.createElement("pathPOSIP");
            pathPOSIP.setTextContent(mapPOS.get(pos)[5]); // pathPOS
            posName.appendChild(pathPOSIP);
            // Определяем pathPOSOOO
            Element pathPOSOOO = document.createElement("pathPOSOOO");
            pathPOSOOO.setTextContent(mapPOS.get(pos)[6]); // pathPOS
            posName.appendChild(pathPOSOOO);
        }
// Запись документа
        try {
            // Сохранить текстовое представление XML документа в файл
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(
                    new File(System.getProperty("user.dir")
                            + File.separator + Sync1C.PREFFILENAME));
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


}
