package kz.hxncus.mc.railcarcoupler.config;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public enum Settings {
    VERSION("version"), PLUGIN_LANGUAGE("lang"), DEBUG("debug"), MAIN_TRAIN_MATERIAL("main_train_material"),
    COUPLER_MATERIAL("coupler_material"), COUPLER_SELECTED_PARTICLE("coupler_selected_particle"), INVENTORY("inventory");
    private final String path;

    Settings(String path) {
        this.path = path;
    }

    public Object getValue() {
        return RailcarCoupler.get().getConfigManager().getSettings().get(path);
    }

    public Object getValue(Object def) {
        return RailcarCoupler.get().getConfigManager().getSettings().get(path, def);
    }

    private void setValue(Object value) {
        setValue(value, true);
    }

    public void setValue(Object value, boolean save) {
        FileConfiguration settings = RailcarCoupler.get().getConfigManager().getSettings();
        settings.set(path, value);
        if (save) {
            try {
                settings.save(RailcarCoupler.get().getDataFolder().toPath().resolve("settings.yml").toFile());
            } catch (Exception e) {
                RailcarCoupler.get().getLogger().severe("Failed to apply changes to settings.yml");
            }
        }
    }

    @Override
    public String toString() {
        return (String) getValue();
    }

    public Boolean toBool() {
        return (Boolean) getValue();
    }

    public Number toNumber() {
        return (Number) getValue();
    }

    public List<String> toStringList() {
        return RailcarCoupler.get().getConfigManager().getSettings().getStringList(path);
    }

    public ConfigurationSection toConfigSection() {
        return RailcarCoupler.get().getConfigManager().getSettings().getConfigurationSection(path);
    }
}
