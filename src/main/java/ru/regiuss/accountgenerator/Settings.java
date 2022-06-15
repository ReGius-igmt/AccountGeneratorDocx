package ru.regiuss.accountgenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private String login;
    private int count;
    private int length;
    private char[] symbols;
    private String in;
    private String out;

    public void load() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("./settings.properties"));
        login = properties.getProperty("login");
        count = Integer.parseInt(properties.getProperty("count"));
        length = Integer.parseInt(properties.getProperty("length"));
        symbols = properties.getProperty("symbols").toCharArray();
        in = properties.getProperty("in");
        out = properties.getProperty("out");
    }

    public String getLogin() {
        return login;
    }

    public int getCount() {
        return count;
    }

    public int getLength() {
        return length;
    }

    public char[] getSymbols() {
        return symbols;
    }

    public String getIn() {
        return in;
    }

    public String getOut() {
        return out;
    }
}
