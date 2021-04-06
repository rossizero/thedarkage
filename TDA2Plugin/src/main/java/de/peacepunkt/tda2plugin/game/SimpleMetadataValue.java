package de.peacepunkt.tda2plugin.game;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class SimpleMetadataValue implements MetadataValue {
    String value;
    Plugin plugin;

    public SimpleMetadataValue(String val, Plugin plugin) {
        this.value = val;
        this.plugin = plugin;
    }
    @Override
    public Object value() {
        return null;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public short asShort() {
        return 0;
    }

    @Override
    public byte asByte() {
        return 0;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public Plugin getOwningPlugin() {
        return plugin;
    }

    @Override
    public void invalidate() {

    }
}
