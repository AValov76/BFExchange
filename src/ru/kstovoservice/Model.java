package ru.kstovoservice;


interface IOData {
    // Сохранение данных по магазинам в файл
    boolean set ();
    // Извлечение данных по магазинам в файл
    boolean get ();
}

class DataModel {
    String[] pos = new String[5];
    private String posName;
    private String pathPOS;
    private String typeofPOS;
    private String repName;
    private String flagName;

    public String getFlagName () {
        return flagName;
    }

    public String getPathPOS () {
        return pathPOS;
    }

    public String getPosName () {
        return posName;
    }

    public String getRepName () {
        return repName;
    }

    public String getTypeofPOS () {
        return typeofPOS;
    }

    @Override
    public String toString () {
        String string = new String();
        for (String s: pos
             ){
            string+= s+" ";
        }
        return string ;
    }
}

public class Model implements IOData {
    public Model () {
    }

    public boolean set () {
        return true;
    }

    public boolean get () {
        return true;
    }
}
