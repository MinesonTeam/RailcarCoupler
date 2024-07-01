package kz.hxncus.mc.railcarcoupler.hook;

import kz.hxncus.mc.railcarcoupler.cache.ICache;
import org.bukkit.inventory.ItemStack;

public interface ItemHook extends ICache {
    ItemStack getItem(String arg);
}
