package de.peacepunkt.tda2plugin.game;

public class MiniarenaCounter {
    private static MiniarenaCounter instance;
    int pigs = 0;
    int kills = 0;

    boolean hasTobeRefreshed = false;

    public static MiniarenaCounter getInstance() {
        if(instance != null) {
            return instance;
        }
        return new MiniarenaCounter();
    }

    public int getPigs() {
        return pigs;
    }

    public int getKills() {
        return kills;
    }

    public void addPig() {
        pigs++;
    }

    public void addKill() {
        kills++;
    }

    public void setHasTobeRefreshed() {
        hasTobeRefreshed = true;
    }
    public boolean isHasTobeRefreshed() {
        return hasTobeRefreshed;
    }
    public void refresh() {
        instance = new MiniarenaCounter();
    }
}
