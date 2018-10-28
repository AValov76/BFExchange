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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


// типа делаю всё по науке, это интерфейс взаимодействия с контроллером, который должен работать с любой моделью (файл XML, SQL база ...)

interface SetOfPOS {

    int sizeMAP();

    void addPOS (String posName, String... data);

    // Полный список POS как массив String[]
    String[] listPOS ();

    void removePOS (String pos);

    void initPOS (Map setPOS);

    boolean existPOS (String pos);
}

// ну а это конкретная реализация модели, файловый вариант

public class Model implements SetOfPOS {

    // путь к файлу с данными на диске
    private static final String FILENAME = "company.xml";

    // данные по умолчанию
    public final static String POSNAME = "Касса №";
    public final static String[] POSDATANAME = {"pathPOS", "typeofPOS", "repName", "flagName"}; // пока не использовал
    public final static String[] POSDATA = {"C:\\Obmen", "Атол", "report.rep", "report.flg"};

    //структура, в которой лежат данные из файла на диске. используется дважды
    // - при инициализации модели, для прогрузки
    private Document document;
    // по сути нахер не надо пока - сделано про запас, под конторы типа АВС с 2 организациями
    private Element company;
    // по сути контейнер для данных из xml - файла. некий посредник между файлом и моделью
    private Map<String, String[]> mapPOS = new HashMap();

    // инициализация модели
    public Model () throws ParserConfigurationException, IOException, SAXException {

        // Корневой элемент

        // проверка на существование файла с данными
        if (loadData()) {

        } else {
            // создание нового файла если такового не было
            document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            company = document.createElement("company");
            document.appendChild(company);
            mapPOS.put(POSNAME+"1", POSDATA);
            addPOStoDOC();
            saveDOC();
        }

        //прогрузка данных из существующего или из нового файла
        loadmapPOS();
    }


    // загрузка данных по POS из файла в mapPOS
    void loadmapPOS () {

        Node root = document.getDocumentElement(); // корневой узел
        NodeList c = root.getChildNodes(); // список детей корневого узла
        String key = new String();
        String[] data = new String[4];

        for (int i = 0; i < c.getLength(); i++) {
            Node pos = c.item(i); // один из детей из списка с - списка компаний
            key = pos.getAttributes().item(0).getTextContent();
            //System.out.println(pos.getAttributes().item(0).getTextContent() + " 1");
            // Если нода не текст, то это книга - заходим внутрь
            if (pos.getNodeType() != Node.TEXT_NODE) {
                NodeList posData = pos.getChildNodes();
                for (int j = 0; j < posData.getLength(); j++) {
                    Node posProp = posData.item(j);
                    //System.out.println(posProp+" 3");
                    // Если нода не текст, то это один из параметров книги - печатаем
                    if (posProp.getNodeType() != Node.TEXT_NODE) {
                        //System.out.println(posProp.getNodeName() + ":" + posProp.getChildNodes().item(0).getTextContent() + " 3");
                        data[j] = posProp.getChildNodes().item(0).getTextContent();
                    }
                }
            }
            mapPOS.put(key, data);
            //System.out.println(mapPOS.get(key)[5]);
        }
    }

    boolean loadData () {

        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(System.getProperty("user.dir")
                    + File.separator + FILENAME);

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

        return true;
    }

    @Override
    public int sizeMAP () {
        return mapPOS.size();
    }

    // добавляет строки в структуру документа (для сохранения)
    public void addPOStoDOC () {

        for (String pos : mapPOS.keySet()
                ) {
            // Элемент типа pos
            Element posName = document.createElement("POS");
            //posName.setTextContent(pos);
            company.appendChild(posName);
            // Еще можно сделать так
            posName.setAttribute("posName", pos);
            // Определяем posName
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
        }
    }

    @Override
    public String[] listPOS () {
        return mapPOS.keySet().toArray(new String[0]); // оно получает String и возвращает его же
    }

    @Override
    public void addPOS (String posName, String... data) {
        mapPOS.put(posName,data);
    }

    @Override
    public void removePOS (String pos) {

    }

    @Override
    public void initPOS (Map setPOS) {

    }

    @Override
    public boolean existPOS (String pos) {
        return false;
    }

    private void saveDOC () throws ParserConfigurationException {

        try {
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

}
