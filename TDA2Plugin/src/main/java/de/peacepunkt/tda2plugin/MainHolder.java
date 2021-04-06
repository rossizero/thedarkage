package de.peacepunkt.tda2plugin;

/**
 * Stores a reference to our plugin Object.
 * Bad coding style, but saves quite some lines.
 */
public class MainHolder {
    private static MainHolder instance;
    public static Main main;

    private MainHolder() {

    }
    void init(Main main) {
        MainHolder.main = main;
    }
    public static MainHolder getInstance() {
        if(instance == null)
            instance = new MainHolder();
        return instance;
    }
}
