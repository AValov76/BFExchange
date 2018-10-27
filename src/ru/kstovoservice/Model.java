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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


// типа делаю всё по науке, это интерфейс взаимодействия с контроллером, который должен работать с любой моделью (файл XML, SQL база ...)

interface setOfPOS {

    void addPOS (String posName, String... data);

    void removePOS (String pos);

    void initSet (Map setPOS);

    boolean existPOS (String pos);
}

// ну а это конкретная реализация модели, файловый вариант

public class Model implements setOfPOS {

    // путь к файлу с данными на диске
    private static final String FILENAME = "company.xml";

    // данные по умолчанию
    private final static String POSNAME = "Касса №1";
    private final static String[] POSDATA = {"C:\\Obmen", "Атол", "report.rep", "report.flg"};

    //структура, в которой лежат данные из файла на диске. используется дважды
    // - при инициализации модели, для прогрузки
    private Document document;
    // по сути нахер не надо пока - сделано про запас, под конторы типа АВС с 2 организациями
    private Element company;
    // по сути контейнер для данных из xml - файла. некий посредник между файлом и моделью
    private Map<String, String[]> mapPOS = new HashMap();

    // инициализация модели
    public Model () throws ParserConfigurationException {

        document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        company = document.createElement("company");

        // проверка на существование файла с данными

        // прогрузка карты POS из файла если такой есть

        // создание нового файла если такового не было
        mapPOS.put(POSNAME,POSDATA);
        addPOStoDOC ();
        saveDOC ();
    }

    // добавляет строки в структуру документа (для сохранения)
    public void addPOStoDOC () {

        for (String pos : mapPOS.keySet()
                ) {
            // Элемент типа pos
            Element posName = document.createElement("posName");
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
    public void addPOS (String posName, String... data) {

    }

    @Override
    public void removePOS (String pos) {

    }

    @Override
    public void initSet (Map setPOS) {

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
