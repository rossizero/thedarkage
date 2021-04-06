package de.peacepunkt.tda2plugin.kits;

import org.bukkit.inventory.ItemStack;

public class KitDescription {
    String name;
    int price;
    ItemStack inventoryMaterial;
    String[] description;

    public KitDescription(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ItemStack getInventoryMaterial() {
        return inventoryMaterial;
    }

    public void setInventoryMaterial(ItemStack inventoryMaterial) {
        this.inventoryMaterial = inventoryMaterial;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public boolean equals(KitDescription d) {
        return d.getName().equals(getName());
    }
}
