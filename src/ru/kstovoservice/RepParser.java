package ru.kstovoservice;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RepParser {
    public static String repParse(String fileName) throws IOException {

        List<String> repXPOS = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8); //пихаем файл построчно в коллекцию строк
        List<String> repIP = new ArrayList<String>(); // отчет для ИП
        List<String> repOOO = new ArrayList<String>(); // отчет для ООО
        int i=0;
        for(String line: repXPOS){
            System.out.println("Новая строка номер"+i+++" "+line);
        }

     return "";
    }
}
