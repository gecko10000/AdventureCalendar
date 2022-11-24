package gecko10000.AdventureCalendar.misc;

import gecko10000.AdventureCalendar.AdventureCalendar;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;

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

    public static String oldDuration(Duration d) {
        return String.format("%01d:%02d:%02d:%02d", d.toDaysPart(), d.toHoursPart(), d.toMinutesPart(), d.toSecondsPart());
    }

    public static String readableDuration(Duration d) {
        if (d.toDays() > 0) return String.format("%.1fd", d.toHours() / 24.0);
        if (d.toHours() > 0) return String.format("%.1fh", d.toMinutes() / 60.0);
        if (d.toMinutes() > 0) return String.format("%.1fm", d.toSeconds() / 60.0);
        return String.format("%.1fs", d.toMillis() / 1000.0);
    }
}
