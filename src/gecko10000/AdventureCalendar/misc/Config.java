package gecko10000.AdventureCalendar.misc;

import redempt.redlib.config.ConfigManager;
import redempt.redlib.config.annotations.ConfigName;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public static Month month = Month.DECEMBER;

    @ConfigName("first-day")
    public static int firstDay = 1;

    @ConfigName("last-day")
    public static int lastDay = 25;

    @ConfigName("sql.use-mySQL")
    public static boolean mySQL = false;

    @ConfigName("sql.ip")
    public static String ip = "192.168.1.1";

    @ConfigName("sql.port")
    public static int port = 3306;

    @ConfigName("sql.username")
    public static String username = "username";

    @ConfigName("sql.password")
    public static String password = "password";

    @ConfigName("sql.database")
    public static String database = "adventurecalendar";

    @ConfigName("items.unclaimed.material")
    public static String unclaimedMaterial = "CHEST_MINECART";

    @ConfigName("items.unclaimed.name")
    public static String unclaimedName = "Day {day}";

    @ConfigName("items.unclaimed.lore")
    public static List<String> unclaimedLore = new ArrayList<>(List.of(
            "&fTime to unlock: %adventurecalendar_timeuntil_{day}%"
    ));

    @ConfigName("items.claimed.material")
    public static String claimedMaterial = "MINECART";

    @ConfigName("items.claimed.name")
    public static String claimedName = "Day {day}";

    @ConfigName("items.claimed.lore")
    public static List<String> claimedLore = new ArrayList<>(List.of(
            "&2Already claimed!"
    ));

    @ConfigName("items.claimed.use-claimed-item-from-day-automatically")
    public static boolean useClaimedItem = true;

    @ConfigName("items.missed.material")
    public static String missedMaterial = "MINECART";

    @ConfigName("items.missed.name")
    public static String missedName = "Day {day}";

    @ConfigName("items.missed.lore")
    public static List<String> missedLore = new ArrayList<>(List.of(
            "&4You missed this one!"
    ));

    @ConfigName("gui.name")
    public static String guiName = "          &4Advent &2Calendar";

    @ConfigName("gui.command-alias")
    public static String calendarAlias = "";

    @ConfigName("messages.wrong-month")
    public static String wrongMonthMessage = "&cIt is not %month% %first% - %last%!";

    @ConfigName("messages.not-allowed")
    public static String notAllowedMessage = "&cYou are not allowed to do this!";

    @ConfigName("messages.no-present")
    public static String noPresentMessage = "&cThere is no present for this day!";

    @ConfigName("messages.missed-present")
    public static String missedPresent = "&cYou missed this present!";

    @ConfigName("messages.cannot-claim-today")
    public static String cannotClaimToday = "&cYou cannot claim this today! Wait %adventurecalendar_timeuntil_{day}%!";

    @ConfigName("messages.already-claimed")
    public static String alreadyClaimed = "&cYou have already claimed this present!";

    @ConfigName("messages.head-database-not-loaded-yet")
    public static String headDatabaseNotLoadedYet = "&cWait a bit before opening this menu!";

    @ConfigName("commands-to-run-on-every-present")
    public static List<String> everyPresentCommands = new ArrayList<>(List.of(
            "broadcast &a%player_name% &ejust claimed their day {day} present! &n/acal&e!"
    ));

    @ConfigName("unlocked-present-word")
    public static String unlockedPresentWord = "Unlocked";

}
