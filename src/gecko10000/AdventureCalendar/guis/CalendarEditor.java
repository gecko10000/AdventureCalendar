package gecko10000.AdventureCalendar.guis;

import gecko10000.AdventureCalendar.AdventureCalendar;
import gecko10000.AdventureCalendar.misc.Config;
import gecko10000.AdventureCalendar.misc.Present;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

public class CalendarEditor {

    private final int size;

    private final AdventureCalendar plugin;
    private final InventoryGUI gui;

    public CalendarEditor(AdventureCalendar plugin) {
        this.size = ItemUtils.minimumChestSize(Config.lastDay - Config.firstDay + 1);
        this.plugin = plugin;
        this.gui = new InventoryGUI(Bukkit.createInventory(null, size,
                AdventureCalendar.msg("&2Edit Advent Calendar")));
        setupEditor();
    }

    private void setupEditor() {
        gui.setDestroyOnClose(false);
        int index = 0;
        for (int i = Config.firstDay; i <= Config.lastDay; i++) {
            Present present = plugin.presents.computeIfAbsent(i, Present::new);
            gui.addButton(index, presentEditButton(present));
            index++;
        }
    }

    private void updateItems() {
        int index = 0;
        for (int i = Config.firstDay; i <= Config.lastDay; i++) {
            Present present = plugin.presents.computeIfAbsent(i, Present::new);
            gui.getInventory().setItem(index, unclaimedItem(present));
            index++;
        }
    }

    private ItemButton presentEditButton(Present present) {
        return ItemButton.create(unclaimedItem(present), evt -> new PresentEditor(plugin, (Player) evt.getWhoClicked(), present));
    }

    public ItemStack unclaimedItem(Present present) {
        return new ItemBuilder(AdventureCalendar.getItem(Config.unclaimedMaterial))
                .setName(AdventureCalendar.msg(present.color() + "Day " + present.day))
                .setCount(present.day)
                .addLore("")
                .addLore(AdventureCalendar.msg("&9Items: &e" + present.getItems().stream().mapToInt(ItemStack::getAmount).sum()))
                .addLore(AdventureCalendar.msg("&2Commands: &e" + present.getCommands().size()));
    }

    public void open(Player player) {
        updateItems();
        gui.open(player);
    }

}
