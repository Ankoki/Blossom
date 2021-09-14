package com.ankoki.blossom.builders;

import com.ankoki.blossom.utils.Chat;
import com.ankoki.blossom.utils.events.gui.ClickEvent;
import com.ankoki.blossom.utils.events.gui.CloseEvent;
import com.ankoki.blossom.utils.events.gui.DragEvent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GUIBuilder {

    public static final Map<Inventory, Map<Integer, ClickEvent>> ALL_CLICK_EVENTS = new ConcurrentHashMap<>();
    public static final Map<Inventory, DragEvent> ALL_DRAG_EVENTS = new ConcurrentHashMap<>();
    public static final Map<Inventory, CloseEvent> ALL_CLOSE_EVENTS = new ConcurrentHashMap<>();

    /**
     * Creates a new GUIBuilder.
     *
     * @param name name of the GUI.
     * @param rows amount of rows in the GUI. Throws an IllegalArgumentException if greater than 6.
     * @return newly created GUIBuilder.
     */
    public static GUIBuilder createGUI(String name, int rows) {
        Validate.isTrue(rows <= 6);
        return new GUIBuilder(Bukkit.createInventory(null, rows * 9, Chat.coloured(name)));
    }

    /**
     * Creates a new GUIBuilder.
     *
     * @param name name of the GUI.
     * @param type the type of inventory to be created.
     * @return newly created GUIBuilder.
     */
    public static GUIBuilder createGUI(String name, InventoryType type) {
        return new GUIBuilder(Bukkit.createInventory(null, type, Chat.coloured(name)));
    }

    /**
     * Creates a new GUIBuilder.
     *
     * @param holder who owns this gui.
     * @param name name of the GUI.
     * @param rows amount of rows in the GUI. Throws an IllegalArgumentException if greater than 6.
     * @return newly created GUIBuilder.
     */
    public static GUIBuilder createGUI(InventoryHolder holder, String name, int rows) {
        Validate.isTrue(rows <= 6);
        return new GUIBuilder(Bukkit.createInventory(holder, rows * 9, Chat.coloured(name)));
    }

    /**
     * Creates a new GUIBuilder.
     *
     * @param holder who owns this gui.
     * @param name name of the GUI.
     * @param type the type of inventory to be created.
     * @return newly created GUIBuilder.
     */
    public static GUIBuilder createGUI(InventoryHolder holder, String name, InventoryType type) {
        return new GUIBuilder(Bukkit.createInventory(holder, type, Chat.coloured(name)));
    }

    /**
     * INTERNAL USE ONLY
     * <p>
     * Creates a new GUIBuilder.
     *
     * @param inventory inventory builder is based around.
     */
    private GUIBuilder(Inventory inventory) {
        this.mainInventory = inventory;
    }

    private final Inventory mainInventory;

    /**
     * Sets an item at the given slot.
     *
     * @param slot slot to be set.
     * @param item item to set the slot to.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder setItem(int slot, ItemStack item) {
        mainInventory.setItem(slot, item);
        return this;
    }


    /**
     * Adds an item to the next available slot.
     *
     * @param item item to add.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder addItem(ItemStack item) {
        mainInventory.addItem(item);
        return this;
    }

    /**
     * Sets an item at the given slot.
     *
     * @param slot slot to be set.
     * @param item item to set the slot to.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder setItem(int slot, ItemBuilder item) {
        mainInventory.setItem(slot, item.build());
        return this;
    }

    /**
     * Adds an item to the next available slot.
     *
     * @param item item to add.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder addItem(ItemBuilder item) {
        mainInventory.addItem(item.build());
        return this;
    }

    /**
     * Sets the border slots of an inventory to a given item.
     *
     * @param item the item to set the border slots to.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder setBorderSlots(ItemStack item) {
        for (int slot : getBorderSlots(this.mainInventory)) {
            mainInventory.setItem(slot, item);
        }
        return this;
    }

    /**
     * Sets the border slots of an inventory to a given material.
     *
     * @param material the material to set the border slots to.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder setBorderSlots(Material material) {
        for (int slot : getBorderSlots(this.mainInventory)) {
            mainInventory.setItem(slot, new ItemStack(material));
        }
        return this;
    }

    /**
     * Sets the click event for certain slots, or all if none are given.
     *
     * @param event event to occur when the given slots are clicked.
     * @param slots slots that will be effected by this event. If no slots are given, it is applied to all.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder setClickEvent(ClickEvent event, int... slots) {
        Map<Integer, ClickEvent> map = ALL_CLICK_EVENTS.containsKey(mainInventory) ? new HashMap<>() : ALL_CLICK_EVENTS.get(mainInventory);
        if (slots.length >= 1) {
            for (int i : slots) {
                map.put(i, event);
            }
        } else {
            for (int i = 0 ; i < mainInventory.getSize() ; i++) {
                map.put(i, event);
            }
        }
        ALL_CLICK_EVENTS.put(mainInventory, map);
        return this;
    }

    /**
     * Sets the drag event for all slots.
     *
     * @param event event to occur when slots are dragged.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder setDragEvent(DragEvent event) {
        ALL_DRAG_EVENTS.put(mainInventory, event);
        return this;
    }

    /**
     * Sets the close event for the inventory.
     *
     * @param event event to occur when the inventory is closed.
     * @return current GUIBuilder for chaining.
     */
    public GUIBuilder setCloseEvent(CloseEvent event) {
        ALL_CLOSE_EVENTS.put(mainInventory, event);
        return this;
    }

    /**
     * INTERNAL USE ONLY
     * <p>
     * Retrieves the border slots of an inventory.
     *
     * @param inventory the inventory to get slots from.
     * @return all slots that are on the border.
     */
    private List<Integer> getBorderSlots(Inventory inventory) {
        List<Integer> slotsList = new ArrayList<>();
        InventoryType type = inventory.getType();
        int rows = 0;
        if (type == InventoryType.CHEST ||
                type == InventoryType.ENDER_CHEST ||
                type == InventoryType.SHULKER_BOX ||
                type == InventoryType.BARREL) rows = inventory.getSize() / 9;
        if (type == InventoryType.DISPENSER ||
                type == InventoryType.DROPPER) rows = 3;
        if (type == InventoryType.HOPPER) return List.of(0, 4);
        if (rows == 0) return List.of();
        int slots = inventory.getSize();
        int slotsPerRow = slots / rows;
        for (int i = 1; i <= rows; i++) {
            slotsList.add(slotsPerRow * (i - 1));
            slotsList.add((slotsPerRow * (i - 1)) + (slotsPerRow - 1));
        }
        for (int i = 1; i <= (slotsPerRow - 2); i++) {
            slotsList.add(i);
        }
        for (int i = slots - 2; i <= ((rows - 1) * slotsPerRow) + 1; i++) {
            slotsList.add(i);
        }
        for (int i = (inventory.getSize() - 1); i >= (inventory.getSize() - slotsPerRow); i--) {
            slotsList.add(i);
        }
        return slotsList;
    }

    /**
     * Builds the current GUI.
     *
     * @return the built inventory.
     */
    public Inventory build() {
        return mainInventory;
    }
}