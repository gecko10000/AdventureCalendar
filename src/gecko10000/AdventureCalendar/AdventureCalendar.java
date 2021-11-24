package gecko10000.AdventureCalendar;

import gecko10000.AdventureCalendar.guis.CalendarEditor;
import gecko10000.AdventureCalendar.misc.Config;
import gecko10000.AdventureCalendar.misc.PAPIExpansion;
import gecko10000.AdventureCalendar.misc.PlayerDataManager;
import gecko10000.AdventureCalendar.misc.Present;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.misc.EventListener;

import java.time.Month;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AdventureCalendar extends JavaPlugin {

    @ConfigValue
    public Map<Integer, Present> presents = ConfigManager.map(Integer.class, Present.class);
    public CalendarEditor calendarEditor;
    public static boolean papi;
    public static boolean headDB;
    private PAPIExpansion papiExpansion;

    public ConfigManager config;
    public ConfigManager presentConfig;

    private static AdventureCalendar instance;

    public void onEnable() {
        instance = this;
        new PlayerDataManager(this);
        new CommandHandler(this);
        reload();
        Bukkit.getOnlinePlayers().forEach(PlayerDataManager::initPlayer);
        new EventListener<>(PlayerJoinEvent.class, evt -> PlayerDataManager.initPlayer(evt.getPlayer()));
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
        config = new ConfigManager(this)
                .addConverter(Month.class, m -> Month.valueOf(m.toUpperCase()), Month::toString)
                .register(Config.class)
                .saveDefaults().load();
        presentConfig = new ConfigManager(this, "presents.yml")
                .register(this)
                .saveDefaults().load();
        presents.keySet().removeIf(i -> i < Config.firstDay || i > Config.lastDay);
        for (int i = Config.firstDay; i <= Config.lastDay; i++) {
            presents.putIfAbsent(i, new Present(i));
        }
        presentConfig.save();
        calendarEditor = new CalendarEditor(this);
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

    public static String msg(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
