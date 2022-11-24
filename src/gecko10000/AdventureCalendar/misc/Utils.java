package gecko10000.AdventureCalendar.misc;

import gecko10000.AdventureCalendar.AdventureCalendar;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

    public static String placeholderMsg(String input, Player player, Present present) {
        return placeholderMsg(input.replace("{day}", present.day + ""), player);
    }

    public static String placeholderMsg(String input, Player player) {
        return msg(AdventureCalendar.papi ? PlaceholderAPI.setPlaceholders(player, input) : input);
    }

    public static String msg(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static ItemStack fillerItem() {
        ItemStack item = new ItemStack(Config.fillerMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET.toString());
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }
}
