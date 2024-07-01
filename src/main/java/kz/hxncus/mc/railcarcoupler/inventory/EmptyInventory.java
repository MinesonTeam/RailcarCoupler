package kz.hxncus.mc.railcarcoupler.inventory;

import org.bukkit.event.inventory.*;

public class EmptyInventory extends AbstractInventory {
    public EmptyInventory(InventoryType type) {
        super(type);
    }

    public EmptyInventory(InventoryType type, String title) {
        super(type, title);
    }

    public EmptyInventory(int size) {
        super(size);
    }

    public EmptyInventory(int size, String title) {
        super(size, title);
    }

    @Override
    public void onInitialize() {

    }

    @Override
    public void onDrag(InventoryDragEvent event) {

    }

    @Override
    public void onClick(InventoryClickEvent event) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }
}
