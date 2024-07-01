package kz.hxncus.mc.railcarcoupler.hook;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemsAdderHook implements ItemHook {
    private final Map<String, ItemStack> cache = new ConcurrentHashMap<>();

    @Override
    public ItemStack getItem(String arg) {
        if (arg.isEmpty()) {
            return new ItemStack(Material.STONE, 1);
        }
        ItemStack cached = this.cache.get(arg);
        if (cached != null) {
            return cached.clone();
        }
        CustomStack customStack = CustomStack.getInstance(arg);
        if (customStack == null) {
            return new ItemStack(Material.STONE, 1);
        }
        ItemStack item = customStack.getItemStack().clone();
        this.cache.put(arg, item);
        return item.clone();
    }

    @Override
    public void clearCache() {
        this.cache.clear();
    }
}
