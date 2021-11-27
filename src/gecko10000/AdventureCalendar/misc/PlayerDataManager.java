package gecko10000.AdventureCalendar.misc;

import gecko10000.AdventureCalendar.AdventureCalendar;
import org.bukkit.Bukkit;
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
        EXECUTOR.execute(() -> {
            sql.execute("CREATE TABLE IF NOT EXISTS claimed (uuid VARCHAR(36) PRIMARY KEY, presents INT)");
            cache = sql.createCache("claimed", "presents", "uuid");
        });
        sql.setCommitInterval(5*60*20);
    }

    public static CompletableFuture<Void> set(OfflinePlayer player, int day, boolean claimed) {
        UUID uuid = player.getUniqueId();
        return CompletableFuture.runAsync(() -> {
            try {
                Integer encoded = cache.select(uuid.toString());
                encoded = encoded == null ? 0 : encoded;
                int newNum = claimed ? encoded | (1 << day - 1) : encoded & ~(1 << day - 1);
                setRaw(uuid, newNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, EXECUTOR);
    }

    public static CompletableFuture<Void> setRaw(UUID uuid, int value) {
        return CompletableFuture.runAsync(() -> {
            try {
                sql.execute("INSERT " + (Config.mySQL ? "" : "OR ") + "IGNORE INTO claimed (uuid, presents) VALUES (?, ?);", uuid.toString(), value);
                cache.update(value, uuid.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, EXECUTOR);
    }

    public static CompletableFuture<Void> initPlayer(OfflinePlayer player) {
        return CompletableFuture.runAsync(() -> {
            try {
                sql.execute("INSERT " + (Config.mySQL ? "" : "OR ") + "IGNORE INTO claimed (uuid, presents) VALUES (?, ?);", player.getUniqueId().toString(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, EXECUTOR);
    }

    public static CompletableFuture<List<Integer>> getClaimedPresents(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> claimed = new ArrayList<>();
            try {
                Integer encoded = cache.select(player.getUniqueId().toString());
                if (encoded != null) {
                    for (int i = 1; i <= 32; i++) {
                        if ((encoded & 1) != 0) {
                            claimed.add(i);
                        }
                        encoded >>= 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return claimed;
        }, EXECUTOR);
    }

    public static CompletableFuture<Boolean> isClaimed(OfflinePlayer player, int day) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer encoded = cache.select(player.getUniqueId().toString());
                return encoded != null && (encoded >> (day-1) & 1) != 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }, EXECUTOR);
    }

    public static CompletableFuture<Void> clearAll() {
        return CompletableFuture.runAsync(() -> {
            try {
                sql.execute("DELETE FROM claimed;");
                cache.clear();
                Bukkit.getOnlinePlayers().forEach(PlayerDataManager::initPlayer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, EXECUTOR);
    }

    public static CompletableFuture<Void> clearAll(int day) {
        return CompletableFuture.runAsync(() -> {
            try {
                sql.execute("UPDATE claimed SET presents = presents & ?;", ~(1 << (day - 1)));
                cache.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, EXECUTOR);
    }

    public static void onDisable() {
        EXECUTOR.shutdown();
    }

}
