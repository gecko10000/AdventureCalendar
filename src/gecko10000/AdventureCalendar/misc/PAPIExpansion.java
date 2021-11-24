package gecko10000.AdventureCalendar.misc;

import gecko10000.AdventureCalendar.AdventureCalendar;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /*
    Allowed formats:
    claimed_total
    claimed_(day)
    timeuntil_next
    timeuntil_(day)
    next
     */
    private final Predicate<String> validPlaceholder = Pattern.compile(
            "claimed_(\\d\\d?|total)|" +
            "timeuntil_(\\d\\d?|next)|" +
            "next").asPredicate();

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (!validPlaceholder.test(params)) {
            return null;
        }
        String[] split = params.split("_");
        String day = split.length > 1 ? split[1] : "";
        if (split[0].equals("claimed")) {
            try {
                return day.equals("total")
                        ? PlayerDataManager.getClaimedPresents(player).get().size() + ""
                        : PlayerDataManager.isClaimed(player, Integer.parseInt(day)).get() ? "Yes" : "No";
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        if (split[0].equals("next")) {
            return LocalDate.now().plusDays(1).getDayOfMonth() + "";
        }
        LocalDate date = LocalDate.now();
        Present present = day.equals("next")
                ? plugin.presents.get(LocalDate.now().plusDays(1).getDayOfMonth())
                : plugin.presents.get(Integer.parseInt(day));
        if (present == null) {
            return null;
        }
        return present.formattedTimeUntilUnlock() + "";
    }
}