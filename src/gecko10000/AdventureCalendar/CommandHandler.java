package gecko10000.AdventureCalendar;

import gecko10000.AdventureCalendar.guis.Calendar;
import gecko10000.AdventureCalendar.misc.Config;
import gecko10000.AdventureCalendar.misc.PlayerDataManager;
import gecko10000.AdventureCalendar.misc.Present;
import gecko10000.AdventureCalendar.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.misc.FormatUtils;
import redempt.redlib.misc.Task;
import redempt.redlib.misc.UserCache;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class CommandHandler {

    private final AdventureCalendar plugin;

    public CommandHandler(AdventureCalendar plugin) {
        this.plugin = plugin;
        UserCache.asyncInit();
        new CommandParser(plugin.getResource("command.rdcml"))
                .setArgTypes(new ArgType<>("offlineplayer", UserCache::getOfflinePlayer)
                                .tabStream(sender -> Bukkit.getOnlinePlayers().stream().map(Player::getName)),
                        new ArgType<>("present", s -> plugin.presents.get(Integer.parseInt(s)))
                                .tabStream(sender -> plugin.presents.keySet().stream().map(i -> i + "")))
                .parse().register(plugin.getName(), this);
    }

    @CommandHook("menu")
    public void menu(Player player) {
        if (LocalDate.now().getMonth() != Config.month && !player.hasPermission("acal.bypass")) {
            player.sendMessage(Utils.placeholderMsg(
                    Config.wrongMonthMessage
                            .replace("%month%", FormatUtils.toTitleCase(Config.month.toString()))
                            .replace("%first%", Config.firstDay + "")
                            .replace("%last%", Config.lastDay + ""),
                    player));
            return;
        }
        if (Config.calendarAlias.equals("")) {
            if (plugin.calendarEditor == null) {
                player.sendMessage(Utils.msg(Config.headDatabaseNotLoadedYet));
                return;
            }
            new Calendar(plugin, player);
        } else {
            Bukkit.dispatchCommand(player, Utils.placeholderMsg(Config.calendarAlias, player));
        }
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        plugin.reload();
        sender.sendMessage(Utils.msg("&aConfigs reloaded!"));
    }

    @CommandHook("edit")
    public void edit(Player player) {
        if (plugin.calendarEditor == null) {
            player.sendMessage(Utils.msg("&cHDB not loaded yet."));
            return;
        }
        plugin.calendarEditor.open(player);
    }

    @CommandHook("claim")
    public void claim(CommandSender sender, Player target, Present present, boolean force) {
        if (force && !sender.hasPermission("acal.claim.force")) {
            sender.sendMessage(Utils.msg(Config.notAllowedMessage));
            return;
        }
        if (!sender.equals(target) && !sender.hasPermission("acal.claim.others")) {
            sender.sendMessage(Utils.msg(Config.notAllowedMessage));
            return;
        }
        present = present == null ? plugin.presents.get(LocalDate.now().getDayOfMonth()) : present;
        if (present == null) {
            sender.sendMessage(Utils.msg(Config.noPresentMessage));
            return;
        }
        present.claim(target, force);
    }

    @CommandHook("resetOne")
    public void reset(CommandSender sender, OfflinePlayer target, Present present) {
        Present finalPresent = present == null ? plugin.presents.get(LocalDate.now().getDayOfMonth()) : present;
        if (finalPresent == null) {
            return;
        }
        PlayerDataManager.set(target, finalPresent.day, false).thenAccept(unused -> {
            sender.sendMessage(Utils.msg("&aReset " + target.getName() + "'s claim status for day " + finalPresent.day + "."));
        });
    }

    @CommandHook("resetAll")
    public void resetAll(CommandSender sender, OfflinePlayer target) {
        PlayerDataManager.setRaw(target.getUniqueId(), 0).thenAccept(unused -> {
            sender.sendMessage(Utils.msg("&aReset advent calendar for " + target.getName() + "."));
        });
    }

    private final Set<CommandSender> resetConfirmations = new HashSet<>();

    @CommandHook("resetAllEveryone")
    public void resetAllEveryone(CommandSender sender) {
        if (!confirm(sender)) {
            return;
        }
        PlayerDataManager.clearAll().thenAccept(unused -> {
            sender.sendMessage(Utils.msg("&aReset advent calendar for everyone."));
        });
    }

    @CommandHook("resetOneEveryone")
    public void resetOneEveryone(CommandSender sender, Present present) {
        if (!confirm(sender)) {
            return;
        }
        PlayerDataManager.clearAll(present.day).thenAccept(unused -> {
            sender.sendMessage(Utils.msg("&aReset day " + present.day + " for everyone."));
        });
    }

    private boolean confirm(CommandSender sender) {
        if (!resetConfirmations.remove(sender)) {
            resetConfirmations.add(sender);
            Task.syncDelayed(() -> resetConfirmations.remove(sender), 200);
            sender.sendMessage(Utils.msg("&cThis is a dangerous command. Run again in the next 10 seconds to confirm."));
            return false;
        }
        return true;
    }

}
