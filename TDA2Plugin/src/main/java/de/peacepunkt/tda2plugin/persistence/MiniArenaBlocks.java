package de.peacepunkt.tda2plugin.persistence;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="miniarenablocks")
public class MiniArenaBlocks {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    String uuid;
    private LocalDateTime timestamp;
    int x;
    int y;
    int z;
    String type;
    public MiniArenaBlocks() {

    }

    public MiniArenaBlocks(Player player, Block block) {
        this.timestamp = LocalDateTime.now();
        this.x = block.getX();
        this.z = block.getZ();
        this.y = block.getY();
        this.type = block.getType().name();
        this.uuid = player.getUniqueId().toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
