package kz.hxncus.mc.railcarcoupler.util;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class Constants {
    public final String YML_EXPANSION = ".yml";
    public final String VERSION = "version";

    public final Set<String> EMBEDDED_LANGUAGES = new HashSet<>(Arrays.asList("langs\\en.yml", "langs\\ru.yml"));
    public final Set<String> SUPPORTED_LANGUAGES = new HashSet<>();
    public final Set<String> FILES = new HashSet<>(Arrays.asList("settings.yml", "data.yml"));
    static {
        File[] files = new File(RailcarCoupler.get().getDataFolder(), "langs").listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(YML_EXPANSION)) {
                    SUPPORTED_LANGUAGES.add(file.getParentFile().getName() + "\\" + file.getName());
                }
            }
        }
        SUPPORTED_LANGUAGES.addAll(EMBEDDED_LANGUAGES);
        FILES.addAll(SUPPORTED_LANGUAGES);
    }
}
