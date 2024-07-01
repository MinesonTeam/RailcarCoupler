package kz.hxncus.mc.railcarcoupler.cache;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Data
public class PlayerCache {
    private UUID minecartUuid;
    private BukkitTask task;
}
