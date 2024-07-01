package kz.hxncus.mc.railcarcoupler.cache;

import lombok.Data;

import java.util.UUID;

@Data
public class TrainCache {
    private UUID coupledVehicle;
    private boolean isMain;
    private Movement movement = Movement.STILL;

    public enum Movement {
        STILL,
        FORWARD,
        BACKWARD
    }
}
