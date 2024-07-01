package kz.hxncus.mc.railcarcoupler.inventory;

import kz.hxncus.mc.railcarcoupler.cache.TrainCache;
import kz.hxncus.mc.railcarcoupler.config.Messages;
import kz.hxncus.mc.railcarcoupler.config.Settings;
import kz.hxncus.mc.railcarcoupler.hook.ItemsAdderHook;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public class TrainControlInventory extends AbstractInventory {
    private final int stillSlot = Settings.INVENTORY.toConfigSection().getInt("still.slot", 10);
    private final int backwardSlot = Settings.INVENTORY.toConfigSection().getInt("backward.slot", 13);
    private final int forwardSlot = Settings.INVENTORY.toConfigSection().getInt("forward.slot", 16);

    public TrainControlInventory() {
        super(27, Messages.TRAIN_CONTROL_INV_TITLE.toString());
    }

    private boolean isItemsAdderItem(String material) {
        return material.startsWith("itemsadder-");
    }

    private ItemStack getItemStack(String material) {
        ItemStack item;
        if (isItemsAdderItem(material)) {
            item = new ItemsAdderHook().getItem(material.substring(11));
        } else {
            item = new ItemStack(Material.getMaterial(material.toUpperCase(Locale.ENGLISH)));
        }
        return item;
    }

    @Override
    public void onInitialize() {
        ConfigurationSection section = Settings.INVENTORY.toConfigSection();
        for (String key : section.getKeys(false)) {
            ItemStack item = getItemStack(section.getString(key + ".material"));
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) {
                continue;
            }
            itemMeta.setDisplayName(section.getString(key + ".name"));
            itemMeta.setLore(section.getStringList(key + ".lore"));
            item.setItemMeta(itemMeta);
            switch (key) {
                case "still":
                    setItem(stillSlot, item);
                    break;
                case "backward":
                    setItem(backwardSlot, item);
                    break;
                case "forward":
                    setItem(forwardSlot, item);
                    break;
            }
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof Minecart) || !plugin.getCacheManager().getTrainCacheMap().containsKey(vehicle)) {
            return;
        }
        TrainCache trainCache = plugin.getCacheManager().getTrainCache(vehicle.getUniqueId());
        if (!trainCache.isMain()) {
            return;
        }
        player.closeInventory();
        int slot = event.getSlot();
        if (slot == stillSlot) {
            trainCache.setMovement(TrainCache.Movement.STILL);
        } else if (slot == forwardSlot) {
            trainCache.setMovement(TrainCache.Movement.FORWARD);
        } else if (slot == backwardSlot) {
            trainCache.setMovement(TrainCache.Movement.BACKWARD);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }
}
