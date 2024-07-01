package kz.hxncus.mc.railcarcoupler.listener;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import kz.hxncus.mc.railcarcoupler.cache.TrainCache;
import kz.hxncus.mc.railcarcoupler.config.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

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
        if (removedTrainCache.isMain()) {
            String materialStr = Settings.MAIN_TRAIN_MATERIAL.toString().toUpperCase(Locale.ENGLISH);
            vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), new ItemStack(Material.getMaterial(materialStr)));
        }
        if (removedTrainCache.getCoupledVehicle() == null) {
            return;
        }
        String materialStr = Settings.COUPLER_MATERIAL.toString().toUpperCase(Locale.ENGLISH);
        ItemStack coupler = new ItemStack(Material.getMaterial(materialStr));
        vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), coupler);
    }

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Minecart) {
            event.setCancelled(true);
            event.setCollisionCancelled(true);
        }
    }
}
