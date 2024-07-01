package kz.hxncus.mc.railcarcoupler.listener;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import kz.hxncus.mc.railcarcoupler.cache.PlayerCache;
import kz.hxncus.mc.railcarcoupler.cache.TrainCache;
import kz.hxncus.mc.railcarcoupler.config.Messages;
import kz.hxncus.mc.railcarcoupler.config.Settings;
import kz.hxncus.mc.railcarcoupler.inventory.IInventory;
import kz.hxncus.mc.railcarcoupler.inventory.TrainControlInventory;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class PlayerListener implements Listener {
    private static RailcarCoupler plugin;
    private final IInventory inventory;

    public PlayerListener(RailcarCoupler plugin) {
        PlayerListener.plugin = plugin;
        inventory = new TrainControlInventory();
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof Minecart) || !plugin.getCacheManager().getTrainCacheMap().containsKey(vehicle)) {
            return;
        }
        TrainCache trainCache = plugin.getCacheManager().getTrainCache(vehicle.getUniqueId());
        if (!trainCache.isMain()) {
            return;
        }
        player.openInventory(inventory.getInventory());
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        Entity rightClicked = event.getRightClicked();
        Minecart minecart;
        if (rightClicked instanceof Minecart) {
            minecart = (Minecart) rightClicked;
        } else if (rightClicked.getVehicle() instanceof Minecart) {
            minecart = (Minecart) rightClicked.getVehicle();
        } else {
            return;
        }
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        String couplerStr = Settings.COUPLER_MATERIAL.toString().toUpperCase(Locale.ENGLISH);
        String mainTrainStr = Settings.MAIN_TRAIN_MATERIAL.toString().toUpperCase(Locale.ENGLISH);
        if (itemInMainHand.getType() == Material.getMaterial(couplerStr)) {
            coupleMinecart(event, minecart, itemInMainHand);
        } else if (itemInMainHand.getType() == Material.getMaterial(mainTrainStr)) {
            event.setCancelled(true);
            plugin.getCacheManager().getTrainCache(minecart.getUniqueId()).setMain(true);
            minecart.setGlowing(true);
            itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
            // TODO Message
        }
    }

    private static void coupleMinecart(PlayerInteractEntityEvent event, Minecart minecart, ItemStack itemInMainHand) {
        event.setCancelled(true);
        PlayerCache playerCache = plugin.getCacheManager().getPlayerCache(event.getPlayer().getUniqueId());
        if (playerCache.getMinecartUuid() == null) {
            playerCache.setMinecartUuid(minecart.getUniqueId());
            playerCache.setTask(plugin.getServer().getScheduler().runTaskLater(plugin, () -> playerCache.setMinecartUuid(null), 6000L));
            Messages.CHOSEN_FIRST_VEHICLE.send(event.getPlayer());
            return;
        }
        Entity entity = plugin.getServer().getEntity(playerCache.getMinecartUuid());
        playerCache.setMinecartUuid(null);
        playerCache.setTask(null);
        if (entity == null || minecart.getLocation().distance(entity.getLocation()) > 3) {
            Messages.FIRST_VEHICLE_NOT_FOUND.send(event.getPlayer());
            return;
        }
        itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
        plugin.getCacheManager().getTrainCache(entity.getUniqueId()).setCoupledVehicle(minecart.getUniqueId());
        Messages.VEHICLES_SUCCESSFULLY_COUPLED.send(event.getPlayer());
    }
}
