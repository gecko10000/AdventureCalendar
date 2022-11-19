package gecko10000.AdventureCalendar.guis;

import gecko10000.AdventureCalendar.AdventureCalendar;
import gecko10000.AdventureCalendar.misc.Present;
import gecko10000.AdventureCalendar.misc.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.inventorygui.PaginationPanel;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.misc.ChatPrompt;
import redempt.redlib.misc.FormatUtils;
import redempt.redlib.misc.Task;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PresentEditor {

    private static final int SIZE = 54;

    private final AdventureCalendar plugin;
    private final Present present;
    private final Player player;
    private final InventoryGUI gui;
    private final PaginationPanel commandPanel;

    public PresentEditor(AdventureCalendar plugin, Player player, Present present) {
        this.plugin = plugin;
        this.present = present;
        this.player = player;
        gui = new InventoryGUI(Bukkit.createInventory(null, SIZE, Utils.msg("&9Items&2                Commands")));
        commandPanel = new PaginationPanel(gui);
        setupEditor();
        gui.open(player);
    }

    private void setupEditor() {
        gui.setReturnsItems(false);
        gui.setDestroyOnClose(false);
        gui.setOnClickOpenSlot(evt -> Task.syncDelayed(() -> {
            present.getItems().clear();
            present.addItems(gui.getOpenSlots().stream().map(gui.getInventory()::getItem).collect(Collectors.toList()));
            updateCount();
            plugin.presentConfig.save();
        }));
        gui.setOnDragOpenSlot(evt -> Task.syncDelayed(() -> {
            present.getItems().clear();
            present.addItems(gui.getOpenSlots().stream().map(gui.getInventory()::getItem).collect(Collectors.toList()));
            updateCount();
            plugin.presentConfig.save();
        }));
        gui.fill(SIZE - 8, SIZE, InventoryGUI.FILLER);
        gui.fill(4, 0, 5, SIZE/9, InventoryGUI.FILLER);
        gui.openSlots(0, 0, 4, SIZE/9 - 1);
        commandPanel.addSlots(5, 0, 9, SIZE/9 - 1);
        gui.addButton(SIZE - 9, ItemButton.create(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setName(Utils.msg("&cBack")), evt -> plugin.calendarEditor.open(player)));
        gui.addButton(SIZE - 4, ItemButton.create(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setName(Utils.msg("&cPrevious")), evt -> commandPanel.prevPage()));
        gui.addButton(SIZE - 1, ItemButton.create(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setName(Utils.msg("&aNext")), evt -> commandPanel.nextPage()));
        updateCount();
        populateItems();
        updateCommandPanel();
    }

    private void updateCount() {
        gui.getInventory().setItem(SIZE - 5, plugin.calendarEditor.unclaimedItem(present));
    }

    private void populateItems() {
        Iterator<Integer> itemSlots = gui.getOpenSlots().iterator();
        int presentItemIndex = 0;
        List<ItemStack> items = present.getItems();
        while (itemSlots.hasNext() && presentItemIndex < items.size()) {
            gui.getInventory().setItem(itemSlots.next(), items.get(presentItemIndex));
            presentItemIndex++;
        }
    }

    private void updateCommandPanel() {
        updateCount();
        plugin.presentConfig.save();
        commandPanel.clear();
        List<String> commands = present.getCommands();
        for (int i = 0; i < commands.size(); i++) {
            int finalI = i;
            String command = commands.get(i);
            ItemStack item = itemForCommand(command);
            commandPanel.addPagedButton(ItemButton.create(item, evt -> {
                if (evt.isShiftClick()) {
                    commands.remove(finalI);
                    updateCommandPanel();
                } else {
                    enterEditMode(commands.get(finalI))
                            .thenAccept(response -> {
                                commands.set(finalI, response);
                                updateCommandPanel();
                                Task.syncDelayed(() -> gui.open(player));
                            });
                }
            }));
        }
        commandPanel.addPagedButton(ItemButton.create(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .setName(Utils.msg("&aAdd Command")), evt -> enterEditMode(null)
                        .thenAccept(response -> {
                            if (response != null) {
                                commands.add(response);
                                updateCommandPanel();
                            }
                            Task.syncDelayed(() -> gui.open(player));
                        })));
    }

    private ItemStack itemForCommand(String command) {
        List<String> split = FormatUtils.lineWrap(command, 32);
        return new ItemBuilder(Material.PAPER)
                .setName(ChatColor.WHITE + split.get(0))
                .addLore(split.subList(1, split.size()).stream().map(s -> ChatColor.WHITE + s).collect(Collectors.toList()))
                .addLore(Stream.of("", "&aLeft click to change the command", "&aShift+click to remove")
                        .map(Utils::msg).collect(Collectors.toList()));
    }

    private CompletableFuture<String> enterEditMode(String original) {
        player.closeInventory();
        CompletableFuture<String> future = new CompletableFuture<>();
        TextComponent editMessage;
        if (original == null) {
            editMessage = new TextComponent("Enter the new command");
        } else {
            editMessage = new TextComponent("Enter the new command (click to insert previous one into chat)");
            editMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.msg("&aClick to insert into chat"))));
            editMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, original));
        }
        editMessage.setColor(ChatColor.YELLOW);
        player.spigot().sendMessage(editMessage);
        ChatPrompt.prompt(player, null, future::complete, cancelReason -> future.complete(original));
        return future;
    }
}
