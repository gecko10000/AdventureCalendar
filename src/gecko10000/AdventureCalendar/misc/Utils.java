package gecko10000.AdventureCalendar.misc;

import gecko10000.AdventureCalendar.AdventureCalendar;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
}
