package de.peacepunkt.tda2plugin.kits;

import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.stats.KitStats;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import javax.persistence.*;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public abstract class AbstractKitSuperclass implements Listener {
    //public static Map<String, AbstractKitSuperclass> subclasses = new HashMap<String, AbstractKitSuperclass>();

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    String uuid;
    int kills;
    int deaths;
    int killStreak;
    int assists;
    @Transient
    int currentKillStreak;


    public AbstractKitSuperclass() {
        //addToSuperclassList();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }

    public boolean isDefault() {
        return getKitDescription().getPrice() == 0;
    }
    //public abstract int getPrice();
    //public abstract ItemStack getInventroyMaterial();
    //public abstract String getName();
    //public abstract String[] getDescription();

    //public abstract void addToSuperclassList();

    public void addKill() {
        this.kills++;
        this.currentKillStreak++;
    }
    public void addDeath() {
        this.deaths++;
        setKillStreak();
        currentKillStreak = 0;
    }
    private void setKillStreak() {
        this.killStreak = Math.max(this.currentKillStreak, killStreak);
    }


    public void addNewStats(KitStats update) {
        this.kills += update.getKills();
        this.deaths += update.getDeaths();
        this.killStreak = Math.max(this.killStreak, update.getKillStreak());
        this.assists += update.getAssists();
    }


    //new
    /*public void register() {
        KitHandler.getInstance().register(this);
    }*/
    public abstract KitDescription getKitDescription();

    /**
     * Returns all ItemStacks needed for that kit.
     * @param player needed to find out the extraLevel for player
     * @return
     */
    public abstract ItemStack[] getKitItemStacks(Player player);

    /**
     *  Effects Player with all Kit related effects or add's runnables etc.
     * @param player
     */
    protected abstract void addEffects(Player player);

    /**
     * Sets the kit for a player
     * @param player
     * @return
     */
    public ItemStack[] setKit(Player player) {
        //clear all effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        //add new effects
        //TODO maybe a delay is needed before calling this function
        addEffects(player);

        //update kit in PlayerHandler
        PlayerHandler.getInstance().updateKit(player, this);
        return getKitItemStacks(player);
    }

    /**
     *
     * @param player
     * @return whether the player currently uses this kit
     */
    protected boolean hasMyKit(Player player) {
        if(PlayerHandler.getInstance().getKit(player) != null)
            return PlayerHandler.getInstance().getKit(player).getKitDescription().equals(getKitDescription());
        return false;
    }

    /**
     *
     * @return whether this Kit is a map specific kit or not
     * used at the end of a round to check if players can play with the same kit as before
     */
    public abstract boolean isMapSpecificKit();
}
