package de.peacepunkt.tda2plugin.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BubiCounter {
    @Id
    @Column(length = 36) //32 hex digits + 4 dashes
    String uuid;
    int count;

    public BubiCounter() {

    }

    public BubiCounter(String uuid){
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public void increase() {
        this.count++;
    }
}
