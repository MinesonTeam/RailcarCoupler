package kz.hxncus.mc.railcarcoupler.inventory.marker;

import org.bukkit.inventory.ItemStack;

public class UnavailableItemMarker implements ItemMarker {
    @Override
    public ItemStack markItem(ItemStack paramItemStack) {
        return paramItemStack;
    }

    @Override
    public ItemStack unmarkItem(ItemStack paramItemStack) {
        return paramItemStack;
    }

    @Override
    public boolean isItemMarked(ItemStack paramItemStack) {
        return false;
    }
}
