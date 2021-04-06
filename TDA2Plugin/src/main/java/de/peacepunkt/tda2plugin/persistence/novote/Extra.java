package de.peacepunkt.tda2plugin.persistence.novote;

import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="extras")
public class Extra {
    private String uuidTarget;
    private String uuidProfiteur;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    public Extra(){

    }
    public Extra(Player uuidProfiteur, Player uuidTarget){
        this.uuidTarget = uuidTarget.getUniqueId().toString();
        this.uuidProfiteur = uuidProfiteur.getUniqueId().toString();
    }

    public String getUuidTarget() {
        return uuidTarget;
    }

    public void setUuidTarget(String uuidTarget) {
        this.uuidTarget = uuidTarget;
    }

    public String getUuidProfiteur() {
        return uuidProfiteur;
    }

    public void setUuidProfiteur(String uuidProfiteur) {
        this.uuidProfiteur = uuidProfiteur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
