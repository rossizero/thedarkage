package de.peacepunkt.tda2plugin.kits.gui;

import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclassDaoImpl;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.kits.kits.*;
import de.peacepunkt.tda2plugin.persistence.xp.Xp;
import de.peacepunkt.tda2plugin.persistence.xp.XpDaoImpl;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExplainBook {
    public ItemStack getBook(Player player, Teem t) {
        List<String> pages = new ArrayList<String>();
        pages.add("Welcome " + player.getName() + " to \n\n"+ChatColor.BOLD+"thedarkage III \n\n" +ChatColor.RESET+ "nice to see you.."); // Page 1
        pages.add(ChatColor.BOLD + "How to \n\n" + ChatColor.RESET +
                "You are currently in your teams spawn room. \n" +
                "From here you can get into the arena by clicking a wool block of your teams color, which currently should be somewhat like"
                + t.getChatColor() + " this" + ChatColor.RESET + ".");
        pages.add("Capture enemy or neutral flags by standing close to them, so teammates can respawn from there! \n\n" +
                "You get points for the /toplist and /xp for each kill or capture!");
        pages.add(ChatColor.BOLD + "usefull commands\n\n" + ChatColor.RESET +
                ChatColor.BOLD +"/sw" + ChatColor.RESET + " to switch team\n" +
                ChatColor.BOLD +"/suicide" + ChatColor.RESET + " to return to spawn in case you got stuck\n" +
                ChatColor.BOLD +"/mvp" + ChatColor.RESET + " to see who is the best player this round\n" +
                ChatColor.BOLD +"/mystats"+ ChatColor.RESET + " to have a look at your stats\n" +
                ChatColor.BOLD +"/toplist" + ChatColor.RESET +" (/toplist [name]/[page]) to look at the toplist");
        pages.add(ChatColor.BOLD +"/recruitmentstats" + ChatColor.RESET + " to get perks like an extra heart and sharpness\n" +
                ChatColor.BOLD +"/entertest" + ChatColor.RESET + " and " + ChatColor.BOLD + "/leavetest" + ChatColor.RESET +" to join or leave the test arena\n" +
                ChatColor.BOLD +"/xp"+ ChatColor.RESET +","+ChatColor.BOLD+" /buy" + ChatColor.RESET +" & " +ChatColor.BOLD+"/vote " + ChatColor.RESET +" to new classes\n" +
                ChatColor.BOLD +"/teamchat"+  ChatColor.RESET +" and " + ChatColor.BOLD + "/globalchat" + ChatColor.RESET + " to switch between chats");
        pages.add(ChatColor.BOLD +"/pm [name]" + ChatColor.RESET + " to constantly chat with one person (/pm to leave)\n" +
                ChatColor.BOLD +"/pm [name] [message]" + ChatColor.RESET +" to send a single message to one person");
        Xp xp = new XpDaoImpl().get(player.getUniqueId().toString());
        int x = 0;
        if(xp != null) {
            x = xp.getXp();
        }
        pages.add(ChatColor.BOLD + "Overview and explanation of the classes" + ChatColor.RESET +"\n\n(recruitment perks in brackets)\n\nTo unlock classes use your /xp in /buy. Each /vote also is worth 50xp!\n" +
                "You have " + ChatColor.RED + x + ChatColor.RESET + "xp");
        pages.addAll(getKitsOfPlayer(player));
        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle("pls. readme");
        bookMeta.setAuthor(player.getName());
        bookMeta.setPages(pages);
        writtenBook.setItemMeta(bookMeta);

        return writtenBook;
    }
    private String makeText(AbstractKitSuperclass a, boolean unlocked) {
        String locked = ChatColor.DARK_RED + "locked";
        String _unlocked = ChatColor.GREEN + "unlocked";
        String entry = ChatColor.BOLD + a.getKitDescription().getName() +" "+ (unlocked ? _unlocked : locked) + "\n" + ChatColor.RESET;
        if (a.getKitDescription().getDescription() != null) {
            if (a.getKitDescription().getDescription().length != 0) {
                for (String s : a.getKitDescription().getDescription()) {
                    entry += s + "\n";
                }
            }
        }
        return entry;
    }
    private List<String> getKitsOfPlayer(Player player) {
        List<String> pages = new ArrayList<>();
        Map<Class<? extends AbstractKitSuperclass>, AbstractKitSuperclass> map = KitHandler.getInstance().getKitSubclassesMap();
        for(Class<? extends AbstractKitSuperclass> kit : KitHandler.getInstance().getKitSubclassesMap().keySet()) {
            AbstractKitSuperclassDaoImpl asdo = new AbstractKitSuperclassDaoImpl<>(kit);
            pages.add(makeText(map.get(kit), asdo.hasKit(player)));
        }

        /*asdo = new AbstractKitSuperclassDaoImpl<KitArcher>(KitArcher.class);
        pages.add(makeText(new KitArcher(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitChaos>(KitChaos.class);
        pages.add(makeText(new KitChaos(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitExecutioner>(KitExecutioner.class);
        pages.add(makeText(new KitExecutioner(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitHealer>(KitHealer.class);
        pages.add(makeText(new KitHealer(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitScout>(KitScout.class);
        pages.add(makeText(new KitScout(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitSense>(KitSense.class);
        pages.add(makeText(new KitSense(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitSpearman>(KitSpearman.class);
        pages.add(makeText(new KitSpearman(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitSwordsman>(KitSwordsman.class);
        pages.add(makeText(new KitSwordsman(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitTrapper>(KitTrapper.class);
        pages.add(makeText(new KitTrapper(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitWaterman>(KitWaterman.class);
        pages.add(makeText(new KitWaterman(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitAcrobat>(KitAcrobat.class);
        pages.add(makeText(new KitAcrobat(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitHalberdier>(KitHalberdier.class);
        pages.add(makeText(new KitHalberdier(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitBerserker>(KitBerserker.class);
        pages.add(makeText(new KitBerserker(), asdo.hasKit(player)));*/

        return pages;
    }
}
