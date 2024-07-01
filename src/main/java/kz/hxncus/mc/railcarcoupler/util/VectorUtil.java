package kz.hxncus.mc.railcarcoupler.util;

import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;

@UtilityClass
public class VectorUtil {
    public final Vector ZERO_VECTOR = new Vector();

    public boolean isZero(Vector vector) {
        return vector.getBlockX() == 0 && vector.getBlockY() == 0 && vector.getBlockZ() == 0;
    }

    public boolean isNotZero(Vector vector) {
        return !isZero(vector);
    }
}