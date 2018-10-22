package ru.kstovoservice;

interface DataModel {
    // Сохранение данных по магазинам в файл
    boolean set ();
    // Извлечение данных по магазинам в файл
    boolean get ();
}

public class Model implements DataModel {
    public Model () {
    }

    public boolean set () {
        return true;
    }

    public boolean get () {
        return true;
    }
}
