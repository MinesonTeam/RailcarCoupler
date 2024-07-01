package kz.hxncus.mc.railcarcoupler;

import kz.hxncus.mc.railcarcoupler.cache.PlayerCache;
import kz.hxncus.mc.railcarcoupler.cache.TrainCache;
import kz.hxncus.mc.railcarcoupler.command.CouplerCommand;
import kz.hxncus.mc.railcarcoupler.listener.EntityListener;
import kz.hxncus.mc.railcarcoupler.listener.PlayerListener;
import kz.hxncus.mc.railcarcoupler.manager.CacheManager;
import kz.hxncus.mc.railcarcoupler.manager.FileManager;
import kz.hxncus.mc.railcarcoupler.manager.InventoryManager;
import kz.hxncus.mc.railcarcoupler.util.QueueUtil;
import kz.hxncus.mc.railcarcoupler.util.VectorUtil;
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

@Getter
public final class RailcarCoupler extends JavaPlugin {
    @Getter
    private static RailcarCoupler instance;
    private FileManager fileManager;
    private CacheManager cacheManager;
    private InventoryManager inventoryManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        registerManagers();
        registerListeners(getServer().getPluginManager());
        registerCommands();
        registerTasks();
    }

    @Override
    public void onDisable() {
        cacheManager.unloadCache();
    }

    private void registerManagers() {
        fileManager = new FileManager(this);
        cacheManager = new CacheManager(this);
        inventoryManager = new InventoryManager(this);
    }

    private void registerListeners(PluginManager manager) {
        manager.registerEvents(new EntityListener(this), this);
        manager.registerEvents(new PlayerListener(this), this);
        manager.registerEvents(inventoryManager, this);
    }

    private void registerCommands() {
        new CouplerCommand(this);
    }

    private Queue<Entity> getCoupledTrains(Entity entity) {
        Queue<Entity> result = new LinkedList<>(Collections.singletonList(entity));
        if (entity == null) {
            return result;
        }
        UUID coupledVehicle = cacheManager.getTrainCache(entity.getUniqueId()).getCoupledVehicle();
        if (coupledVehicle == null) {
            return result;
        }
        result.addAll(getCoupledTrains(getServer().getEntity(coupledVehicle)));
        return result;
    }

    private void registerTasks() {
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                PlayerCache playerCache = cacheManager.getPlayerCache(onlinePlayer.getUniqueId());
                if (playerCache.getMinecartUuid() == null) {
                    continue;
                }
                Entity vehicle = getServer().getEntity(playerCache.getMinecartUuid());
                if (vehicle != null) {
                    BoundingBox boundingBox = vehicle.getBoundingBox();
                    vehicle.getWorld().spawnParticle(Particle.valueOf(getConfig().getString("coupler_selected_effect")), boundingBox.getCenterX(), boundingBox.getMaxY() + 0.5, boundingBox.getCenterZ(), 1);
                }
            }
        }, 0L, 20L);

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Map.Entry<Entity, TrainCache> entry : cacheManager.getTrainCacheMap().entrySet()) {
                if (!entry.getValue().isMain()) {
                    continue;
                }
                Queue<Entity> coupledTrains = getCoupledTrains(entry.getKey());
                if (entry.getValue().getMovement() == TrainCache.Movement.BACKWARD) {
                    QueueUtil.reverse(coupledTrains);
                }
                for (Entity leader = coupledTrains.poll(); leader != null && coupledTrains.peek() != null; leader = coupledTrains.poll()) {
                    Entity follower = coupledTrains.peek();
                    if (entry.getValue().getMovement() == TrainCache.Movement.STILL) {
                        leader.setVelocity(VectorUtil.ZERO_VECTOR);
                        if (follower != null) {
                            follower.setVelocity(VectorUtil.ZERO_VECTOR);
                        }
                    } else {
                        if (follower == null) {
                            continue;
                        }
                        Vector leaderPosition = leader.getLocation().toVector();
                        Vector followerPosition = follower.getLocation().toVector();
                        double distance = leaderPosition.distance(followerPosition);
                        if (distance > 1.5D) {
                            // Скорость следования
                            Vector normalized = leaderPosition.subtract(followerPosition).normalize();
                            leader.setVelocity(normalized);
                            follower.setVelocity(normalized);
                        } else if (distance < 1.2D) {
                            // Корректировка позиции
                            follower.setVelocity(followerPosition.subtract(leaderPosition).normalize().multiply(0.1));
                        } else {
                            follower.setVelocity(VectorUtil.ZERO_VECTOR);
                        }
                    }
                }
            }

        }, 0L, 20L);
    }
}
