package kz.hxncus.mc.railcarcoupler.manager;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import kz.hxncus.mc.railcarcoupler.cache.PlayerCache;
import kz.hxncus.mc.railcarcoupler.cache.TrainCache;
import kz.hxncus.mc.railcarcoupler.util.Constants;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.PreferencesFactory;

@Getter
@EqualsAndHashCode
public class CacheManager {
    @Getter
    private static RailcarCoupler plugin;
    private final Map<UUID, PlayerCache> playerCacheMap = new ConcurrentHashMap<>();
    private final Map<Entity, TrainCache> trainCacheMap = new ConcurrentHashMap<>();

    public CacheManager(RailcarCoupler plugin) {
        CacheManager.plugin = plugin;
        File file = new File(plugin.getDataFolder() + "\\data.yml");
        try {
            file.createNewFile();
        } catch (IOException ignored) {
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(config.getString(key + ".uuid", ""));
            if (plugin.getServer().getEntity(uuid) == null) {
                continue;
            }
            TrainCache trainCache = getTrainCache(uuid);
            trainCache.setCoupledVehicle(UUID.fromString(config.getString(key + ".coupledVehicle", "")));
            trainCache.setMain(config.getBoolean(key + ".isMain", false));
        }
        try {
            Files.delete(file.toPath());
        } catch (IOException ignored) {
        }
    }

    public void unloadCache() {
        File file = new File(plugin.getDataFolder() + "\\data.yml");
        try {
            file.createNewFile();
        } catch (IOException ignored) {
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int i = 0;
        for (Map.Entry<Entity, TrainCache> entry : trainCacheMap.entrySet()) {
            if (entry.getValue().getCoupledVehicle() == null) {
                continue;
            }
            config.set(i + ".uuid", entry.getKey().getUniqueId().toString());
            config.set(i + ".coupledVehicle", entry.getValue().getCoupledVehicle().toString());
            config.set(i++ + ".isMain", entry.getValue().isMain());
        }
        try {
            config.save(file);
        } catch (IOException ignored) {
        }
    }

    public PlayerCache getPlayerCache(final UUID uuid) {
        return playerCacheMap.computeIfAbsent(uuid, uuids -> new PlayerCache());
    }

    public PlayerCache removePlayerCache(final UUID uuid) {
        return playerCacheMap.remove(uuid);
    }

    public TrainCache getTrainCache(final UUID uuid) {
        return trainCacheMap.computeIfAbsent(plugin.getServer().getEntity(uuid), uuids -> new TrainCache());
    }

    public TrainCache removeTrainCache(final Entity entity) {
        return trainCacheMap.remove(entity);
    }

    public TrainCache removeTrainCache(final UUID uuid) {
        return trainCacheMap.remove(plugin.getServer().getEntity(uuid));
    }
}
