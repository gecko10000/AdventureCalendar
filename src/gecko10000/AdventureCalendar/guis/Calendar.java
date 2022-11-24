package gecko10000.AdventureCalendar.guis;

import gecko10000.AdventureCalendar.AdventureCalendar;
import gecko10000.AdventureCalendar.misc.Config;
import gecko10000.AdventureCalendar.misc.Present;
import gecko10000.AdventureCalendar.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.misc.Task;

import java.util.*;
import java.util.stream.Collectors;

public class Calendar {

    private static final int SIZE = 54;

    private final AdventureCalendar plugin;
    private final InventoryGUI gui;
    private final Player player;

    public Calendar(AdventureCalendar plugin, Player player) {
        this.plugin = plugin;
        this.gui = new InventoryGUI(Bukkit.createInventory(null, SIZE, Utils.msg(Config.guiName)));
        this.player = player;
        setupCalendar();
        gui.open(player);
    }

    Map<Integer, Present> slots = new HashMap<>();

    private void setupCalendar() {
        gui.fill(0, SIZE, Utils.fillerItem());
        Set<Present> presents = new HashSet<>(plugin.presents.values());
        Iterator<Present> presentIterator = presents.iterator();
        int slot = 1;
        while (slot < 53 && presentIterator.hasNext()) {
            Present present = presentIterator.next();
            gui.addButton(slot, buttonFor(present));
            slots.put(slot, present);
            slot += (slot == 43 ? 4 : 2);
        }
        Task task = Task.syncRepeating(this::updateItems, 20, 20);
        gui.setOnDestroy(task::cancel);
    }

    private void updateItems() {
        slots.forEach((slot, present) -> gui.getInventory().setItem(slot, present.displayItemFor(player)));
    }


    private ItemButton buttonFor(Present present) {
        return ItemButton.create(present.displayItemFor(player), evt -> present.claim(player, false));
    }

}
