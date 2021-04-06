package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;


public class MapVoteUtils {
    public static void openBook(Player player, List<String> names) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        ComponentBuilder builder = new ComponentBuilder();
        builder.append("Click to vote for one of the following maps: \n");
        for(int i = 0; i < Main.numVoteMaps; i++) {
            //mapvote command is in RoundCommands
            builder.append(names.get(i) + "\n").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tda2plugins:mapvote " + i));
        }
        bookMeta.spigot().addPage(builder.create());
        bookMeta.setTitle("vote");
        bookMeta.setAuthor("thedarkage");
        book.setItemMeta(bookMeta);
        player.openBook(book);
    }
}
