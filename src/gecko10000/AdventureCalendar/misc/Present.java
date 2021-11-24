package gecko10000.AdventureCalendar.misc;

import gecko10000.AdventureCalendar.AdventureCalendar;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigPostInit;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.time.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ConfigMappable
public class Present {

    public int day;

    @ConfigPath
    private String dayString;

    @ConfigValue
    private List<String> commands = ConfigManager.list(String.class);

    @ConfigValue
    private List<ItemStack> items = ConfigManager.list(ItemStack.class);

    private Present() {}

    @ConfigPostInit
    private void postInit() {
        this.day = Integer.parseInt(dayString);
    }

    public Present(int day) {
        this.day = day;
        dayString = "" + day;
    }

    public Present add(String command) {
        commands.add(command);
        return this;
    }

    public Present addItems(List<ItemStack> items) {
        items.forEach(this::add);
        return this;
    }

    public Present add(ItemStack item) {
        if (item != null) {
            items.add(item.clone());
        }
        return this;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public String formattedTimeUntilUnlock() {
        long seconds = secondsUntilUnlock();
        if (seconds <= 0) {
            return "Unlocked";
        }
        return String.format("%01d:%02d:%02d:%02d", seconds/86400, (seconds % 86400) / 3600, (seconds % 3600) / 60, (seconds % 60));
    }

    public long secondsUntilUnlock() {
        return Duration.between(LocalDateTime.now(),
                LocalTime.MIDNIGHT.atDate(LocalDate.of(LocalDate.now().getYear(), Config.month, day))).getSeconds();
    }

    public ItemStack displayItemFor(Player player) {
        try {
            return item(player, PlayerDataManager.isClaimed(player, day).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ItemStack(Material.BARRIER);
        }
    }

    public ItemStack item(Player player, boolean claimed) {
        String name = AdventureCalendar.msg(color() + (claimed ? Config.claimedName : isVoid() ? Config.missedName : Config.unclaimedName).replace("%day%", day + ""));
        name = AdventureCalendar.papi ? PlaceholderAPI.setPlaceholders(player, name) : name;
        List<String> lore = (claimed ? Config.claimedLore : isVoid() ? Config.missedLore : Config.unclaimedLore).stream()
                .map(s -> s.replace("%day%", day + ""))
                .map(s -> AdventureCalendar.papi ? PlaceholderAPI.setPlaceholders(player, s) : s)
                .collect(Collectors.toList());
        ItemBuilder item = new ItemBuilder(AdventureCalendar.getItem(claimed
                ? items.size() == 0 || !Config.useClaimedItem
                    ? Config.claimedMaterial
                    : items.get(0).getType().toString()
                : isVoid()
                    ? Config.missedMaterial
                    : Config.unclaimedMaterial))
                .setName(name).addLore(lore).setCount(day).addItemFlags(ItemFlag.values());
        if (!claimed && day != LocalDate.now().getDayOfMonth()) {
            return item;
        }
        return item.addEnchant(Enchantment.DURABILITY, 1);
    }

    public boolean isVoid() {
        LocalDate date = LocalDate.now();
        return date.isAfter(LocalDate.of(date.getYear(), Config.month, day));
    }

    public void claim(Player player, boolean force) {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        if (day != dayOfMonth && !force) {
            player.sendMessage(AdventureCalendar.msg("&cYou cannot claim that present today!"));
            return;
        }
        PlayerDataManager.isClaimed(player, day)
            .thenAccept(claimed -> {
                if (!claimed || force) {
                    execute(player);
                    PlayerDataManager.set(player, day, true);
                } else {
                    player.sendMessage(AdventureCalendar.msg("&cYou have already claimed this present!"));
                }
            });
    }

    public void execute(Player player) {
        ItemUtils.give(player, items.toArray(new ItemStack[0]));
        for (String command : commands) {
            if (AdventureCalendar.papi) {
                command = PlaceholderAPI.setPlaceholders(player, command);
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public String color() {
        return day % 2 == 0 ? "&c" : "&a";
    }

}
