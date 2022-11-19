package gecko10000.AdventureCalendar;

import gecko10000.AdventureCalendar.guis.CalendarEditor;
import gecko10000.AdventureCalendar.misc.*;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.misc.EventListener;

import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@ConfigMappable
public class AdventureCalendar extends JavaPlugin {

    public Map<Integer, Present> presents = new HashMap<>();
    public transient CalendarEditor calendarEditor = null;
    public static boolean papi;
    public static boolean headDB;
    private transient PAPIExpansion papiExpansion;

    public transient ConfigManager config;
    public transient ConfigManager presentConfig;

    private static AdventureCalendar instance;

    public void onEnable() {
        instance = this;
        reload();
        new PlayerDataManager(this);
        new CommandHandler(this);
        Bukkit.getOnlinePlayers().forEach(PlayerDataManager::initPlayer);
        new EventListener<>(PlayerJoinEvent.class, evt -> PlayerDataManager.initPlayer(evt.getPlayer()));
        if (headDB) {
            // in a separate file so we don't get ClassNotFoundExceptions
            HeadDBLoader.dbLoad().thenRun(() -> calendarEditor = new CalendarEditor(this));
        } else {
            calendarEditor = new CalendarEditor(this);
        }
    }

    public void onDisable() {
        PlayerDataManager.onDisable();
        PlayerDataManager.sql.commit();
        PlayerDataManager.sql.close();
    }

    public void reload() {
        papi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (papi && papiExpansion == null) {
            papiExpansion = new PAPIExpansion(this);
        }
        headDB = Bukkit.getPluginManager().isPluginEnabled("HeadDatabase");
        config = ConfigManager.create(this)
                .addConverter(Month.class, m -> Month.valueOf(m.toUpperCase()), Month::toString)
                .target(Config.class)
                .saveDefaults().load();
        presentConfig = ConfigManager.create(this, "presents.yml")
                .target(this)
                .saveDefaults().load();
        presents.keySet().removeIf(i -> i < Config.firstDay || i > Config.lastDay);
        for (int i = Config.firstDay; i <= Config.lastDay; i++) {
            presents.putIfAbsent(i, new Present(i));
        }
        presentConfig.save();
    }

    private static final Predicate<String> HDB_PATTERN = Pattern.compile("hdb-[0-9]+").asPredicate();

    public static ItemStack getItem(String materialOrHead) {
        if (!HDB_PATTERN.test(materialOrHead)) {
            return new ItemStack(Optional.ofNullable(Material.getMaterial(materialOrHead)).orElse(Material.BARRIER));
        }
        if (!headDB) {
            instance.getLogger().warning("Attempted to use a Head Database head (" + materialOrHead + "), but no Head Database was found");
            return new ItemStack(Material.BARRIER);
        }
        return new HeadDatabaseAPI().getItemHead(materialOrHead.split("-")[1]);
    }

    public static String placeholderMsg(String input, Player player, Present present) {
        return placeholderMsg(input.replace("{day}", present.day + ""), player);
    }

    public static String placeholderMsg(String input, Player player) {
        return msg(papi ? PlaceholderAPI.setPlaceholders(player, input) : input);
    }

    public static String msg(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
