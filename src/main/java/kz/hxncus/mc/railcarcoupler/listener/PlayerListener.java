package kz.hxncus.mc.railcarcoupler.listener;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import kz.hxncus.mc.railcarcoupler.cache.PlayerCache;
import kz.hxncus.mc.railcarcoupler.cache.TrainCache;
import kz.hxncus.mc.railcarcoupler.inventory.IInventory;
import kz.hxncus.mc.railcarcoupler.inventory.TrainControlInventory;
import kz.hxncus.mc.railcarcoupler.util.Messages;
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
        if (itemInMainHand.getType() == Material.getMaterial(plugin.getConfig().getString("coupler_material", "CHAIN").toUpperCase(Locale.ENGLISH))) {
            coupleMinecart(event, minecart, itemInMainHand);
        } else if (itemInMainHand.getType() == Material.getMaterial(plugin.getConfig().getString("main_train_material", "FURNACE").toUpperCase(Locale.ENGLISH))) {
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
            Messages.CHOSEN_FIRST_VEHICLE.sendMessage(event.getPlayer());
            return;
        }
        Entity entity = plugin.getServer().getEntity(playerCache.getMinecartUuid());
        playerCache.setMinecartUuid(null);
        playerCache.setTask(null);
        if (entity == null || minecart.getLocation().distance(entity.getLocation()) > 3) {
            Messages.FIRST_VEHICLE_NOT_FOUND.sendMessage(event.getPlayer());
            return;
        }
        itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
        plugin.getCacheManager().getTrainCache(entity.getUniqueId()).setCoupledVehicle(minecart.getUniqueId());
        Messages.VEHICLES_SUCCESSFULLY_COUPLED.sendMessage(event.getPlayer());
    }
}
