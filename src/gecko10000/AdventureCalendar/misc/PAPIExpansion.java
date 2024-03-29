package gecko10000.AdventureCalendar.misc;

import gecko10000.AdventureCalendar.AdventureCalendar;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PAPIExpansion extends PlaceholderExpansion {

    private final AdventureCalendar plugin;

    public PAPIExpansion(AdventureCalendar plugin) {
        this.plugin = plugin;
        this.register();
    }

    @Override
    public String getIdentifier() {
        return plugin.getName().toLowerCase();
    }

    @Override
    public String getAuthor() {
        return String.join(" ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    private final Predicate<String> validPlaceholder = Pattern.compile(
            "claimed_(\\d\\d?|total)|" +
            "timeuntil_(\\d\\d?|next)|" +
            "next|current").asMatchPredicate();

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        int currentDay = LocalDate.now().getDayOfMonth();
        params = params.replace("{day}", currentDay + "");
        if (!validPlaceholder.test(params)) {
            return null;
        }
        String[] split = params.split("_");
        String day = split.length > 1 ? split[1] : "";
        if (split[0].equals("claimed")) {
            try {
                return day.equals("total")
                        ? PlayerDataManager.getClaimedPresents(player).get().size() + ""
                        : PlayerDataManager.isClaimed(player, Integer.parseInt(day)).get()
                            ? PlaceholderAPIPlugin.booleanTrue()
                            : PlaceholderAPIPlugin.booleanFalse();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        if (split[0].equals("next")) {
            return getSoonestPresent() + "";
        }
        if (split[0].equals("current")) {
            return (getSoonestPresent() - 1) + "";
        }
        Present present = day.equals("next")
                ? plugin.presents.get(getSoonestPresent())
                : plugin.presents.get(Integer.parseInt(day));
        if (present == null) {
            return null;
        }
        return present.formattedTimeUntilUnlock() + "";
    }

    private int getSoonestPresent() {
        LocalDate now = LocalDate.now();
        // return first present if not in the month
        if (Config.month != now.getMonth()) return plugin.presents.keySet().stream()
                .sorted()
                .findFirst().orElse(-1);
        return plugin.presents.keySet().stream()
                .sorted()
                .dropWhile(i -> i <= now.getDayOfMonth())
                .findFirst().orElse(-1);
    }

}
