package gecko10000.AdventureCalendar.misc;

import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.time.Month;
import java.util.List;

public class Config {

    @ConfigValue
    public static Month month = Month.DECEMBER;

    @ConfigValue("first-day")
    public static int firstDay = 1;

    @ConfigValue("last-day")
    public static int lastDay = 25;

    @ConfigValue("sql.use-mySQL")
    public static boolean mySQL = false;

    @ConfigValue("sql.ip")
    public static String ip = "192.168.1.1";

    @ConfigValue("sql.port")
    public static int port = 3306;

    @ConfigValue("sql.username")
    public static String username = "username";

    @ConfigValue("sql.password")
    public static String password = "password";

    @ConfigValue("sql.database")
    public static String database = "adventurecalendar";

    @ConfigValue("items.unclaimed.material")
    public static String unclaimedMaterial = "CHEST_MINECART";

    @ConfigValue("items.unclaimed.name")
    public static String unclaimedName = "Day {day}";

    @ConfigValue("items.unclaimed.lore")
    public static List<String> unclaimedLore = ConfigManager.stringList("&fTime to unlock: %adventurecalendar_timeuntil_{day}%");

    @ConfigValue("items.claimed.material")
    public static String claimedMaterial = "MINECART";

    @ConfigValue("items.claimed.name")
    public static String claimedName = "Day {day}";

    @ConfigValue("items.claimed.lore")
    public static List<String> claimedLore = ConfigManager.stringList("&2Already claimed!");

    @ConfigValue("items.claimed.use-claimed-item-from-day-automatically")
    public static boolean useClaimedItem = true;

    @ConfigValue("items.missed.material")
    public static String missedMaterial = "MINECART";

    @ConfigValue("items.missed.name")
    public static String missedName = "Day {day}";

    @ConfigValue("items.missed.lore")
    public static List<String> missedLore = ConfigManager.stringList("&4You missed this one!");

    @ConfigValue("gui.name")
    public static String guiName = "          &4Advent &2Calendar";

    @ConfigValue("gui.command-alias")
    public static String calendarAlias = "";

    @ConfigValue("messages.wrong-month")
    public static String wrongMonthMessage = "&cIt is not %month% %first% - %last%!";

    @ConfigValue("messages.not-allowed")
    public static String notAllowedMessage = "&cYou are not allowed to do this!";

    @ConfigValue("messages.no-present")
    public static String noPresentMessage = "&cThere is no present for this day!";

    @ConfigValue("messages.missed-present")
    public static String missedPresent = "&cYou missed this present!";

    @ConfigValue("messages.cannot-claim-today")
    public static String cannotClaimToday = "&cYou cannot claim this today! Wait %adventurecalendar_timeuntil_{day}%!";

    @ConfigValue("messages.already-claimed")
    public static String alreadyClaimed = "&cYou have already claimed this present!";

    @ConfigValue("commands-to-run-on-every-present")
    public static List<String> everyPresentCommands = ConfigManager.stringList(
            "broadcast &a%player_name% &ejust claimed their day {day} present! &n/acal&e!"
    );

    @ConfigValue("unlocked-present-word")
    public static String unlockedPresentWord = "Unlocked";

}
