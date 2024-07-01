package kz.hxncus.mc.railcarcoupler.inventory;

import kz.hxncus.mc.railcarcoupler.cache.TrainCache;
import kz.hxncus.mc.railcarcoupler.config.Messages;
import kz.hxncus.mc.railcarcoupler.hook.ItemsAdderHook;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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
    private final FileConfiguration config = plugin.getConfig();
    private final int stillSlot = config.getInt("inventory.still_slot", 10);
    private final int backwardSlot = config.getInt("inventory.backward_slot", 13);
    private final int forwardSlot = config.getInt("inventory.forward_slot", 16);

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
        String stillMaterial = config.getString("inventory.still_material", "OAK_BUTTON");
        String backwardMaterial = config.getString("inventory.backward_material", "OAK_BUTTON");
        String forwardMaterial = config.getString("inventory.forward_material", "OAK_BUTTON");

        ItemStack stillItem = getItemStack(stillMaterial);
        ItemMeta stillItemMeta = stillItem.getItemMeta();
        if (stillItemMeta != null) {
            stillItemMeta.setDisplayName(config.getString("inventory.still_name"));
            stillItemMeta.setLore(config.getStringList("inventory.still_lore"));
            stillItem.setItemMeta(stillItemMeta);
        }
        ItemStack backwardItem = getItemStack(backwardMaterial);
        ItemMeta backwardItemMeta = backwardItem.getItemMeta();
        if (backwardItemMeta != null) {
            backwardItemMeta.setDisplayName(config.getString("inventory.backward_name"));
            backwardItemMeta.setLore(config.getStringList("inventory.backward_lore"));
            backwardItem.setItemMeta(backwardItemMeta);
        }
        ItemStack forwardItem = getItemStack(forwardMaterial);
        ItemMeta forwardItemMeta = forwardItem.getItemMeta();
        if (forwardItemMeta != null) {
            forwardItemMeta.setDisplayName(config.getString("inventory.forward_name"));
            forwardItemMeta.setLore(config.getStringList("inventory.forward_lore"));
            forwardItem.setItemMeta(forwardItemMeta);
        }

        setItem(stillSlot, stillItem);
        setItem(backwardSlot, backwardItem);
        setItem(forwardSlot, forwardItem);
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
