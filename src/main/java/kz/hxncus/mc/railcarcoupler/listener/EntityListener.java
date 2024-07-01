package kz.hxncus.mc.railcarcoupler.listener;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import kz.hxncus.mc.railcarcoupler.cache.TrainCache;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EntityListener implements Listener {
    private static RailcarCoupler plugin;

    public EntityListener(RailcarCoupler plugin) {
        EntityListener.plugin = plugin;
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Minecart)) {
            return;
        }
        TrainCache removedTrainCache = plugin.getCacheManager().removeTrainCache(vehicle);
        if (removedTrainCache == null) {
            return;
        }
        ItemStack coupler = new ItemStack(Material.getMaterial(plugin.getConfig().getString("coupler_material", "CHAIN").toUpperCase(Locale.ENGLISH)));
        vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), coupler);
        if (removedTrainCache.isMain()) {
            String material = plugin.getConfig().getString("main_train_material", "FURNACE").toUpperCase(Locale.ENGLISH);
            vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), new ItemStack(Material.getMaterial(material)));
        }
    }

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Minecart) {
            event.setCancelled(true);
            event.setCollisionCancelled(true);
        }
    }
}
