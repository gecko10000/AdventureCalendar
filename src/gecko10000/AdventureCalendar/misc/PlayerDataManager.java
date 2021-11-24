package gecko10000.AdventureCalendar.misc;

import gecko10000.AdventureCalendar.AdventureCalendar;
import org.bukkit.OfflinePlayer;
import redempt.redlib.sql.SQLCache;
import redempt.redlib.sql.SQLHelper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerDataManager {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static PlayerDataManager manager;

    private final AdventureCalendar plugin;
    public static SQLHelper sql;
    private static SQLCache cache;

    public PlayerDataManager(AdventureCalendar plugin) {
        this.plugin = plugin;
        if (manager != null) {
            return;
        }
        manager = this;
        startDatabase();
    }

    private void startDatabase() {
        Connection connection = Config.mySQL
                ? SQLHelper.openMySQL(Config.ip, Config.port, Config.username, Config.password, Config.database)
                : SQLHelper.openSQLite(plugin.getDataFolder().toPath().resolve("data.db"));
        sql = new SQLHelper(connection);
        EXECUTOR.execute(() -> sql.execute("CREATE TABLE IF NOT EXISTS claimed (uuid VARCHAR(36) PRIMARY KEY, presents INT)"));
        cache = sql.createCache("claimed", "presents", "uuid");
        sql.setCommitInterval(5*60*20);
    }

    public static CompletableFuture<Void> set(OfflinePlayer player, int day, boolean claimed) {
        UUID uuid = player.getUniqueId();
        return CompletableFuture.runAsync(() -> {
            Integer encoded = cache.select(uuid);
            encoded = encoded == null ? 0 : encoded;
            int newNum = claimed ? encoded | (1 << day - 1) : encoded & ~(1 << day - 1);
            setRaw(uuid, newNum);
        }, EXECUTOR);
    }

    public static CompletableFuture<Void> setRaw(UUID uuid, int value) {
        return CompletableFuture.runAsync(() -> {
            sql.execute("INSERT OR IGNORE INTO claimed (uuid, presents) VALUES (?, ?);", uuid, value);
            cache.update(value, uuid);
        });
    }

    public static CompletableFuture<List<Integer>> getClaimedPresents(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> claimed = new ArrayList<>();
            Integer encoded = cache.select(player.getUniqueId());
            if (encoded != null) {
                for (int i = 1; i <= 32; i++) {
                    if ((encoded & 1) != 0) {
                        claimed.add(i);
                    }
                    encoded >>= 1;
                }
            }
            return claimed;
        }, EXECUTOR);
    }

    public static CompletableFuture<Boolean> isClaimed(OfflinePlayer player, int day) {
        return CompletableFuture.supplyAsync(() -> {
            Integer encoded = cache.select(player.getUniqueId());
            return encoded != null && (encoded >> (day-1) & 1) != 0;
        }, EXECUTOR);
    }

    public static void onDisable() {
        EXECUTOR.shutdown();
    }

}
