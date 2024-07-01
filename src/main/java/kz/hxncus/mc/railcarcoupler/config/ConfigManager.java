package kz.hxncus.mc.railcarcoupler.config;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@EqualsAndHashCode
public class ConfigManager {
    private final RailcarCoupler plugin;
    private final YamlConfiguration defaultSettings;
    private final YamlConfiguration defaultLanguage;
    private YamlConfiguration settings;
    private YamlConfiguration language;
    private File langsFolder;

    public ConfigManager(RailcarCoupler plugin) {
        this.plugin = plugin;
        this.defaultSettings = extractDefault("settings.yml");
        this.defaultLanguage = extractDefault("langs/en.yml");
        this.validateConfigs();
    }

    public YamlConfiguration getSettings() {
        return settings == null ? defaultSettings : settings;
    }

    public File getSettingsFile() {
        return new File(plugin.getDataFolder(), "settings.yml");
    }

    public YamlConfiguration getLanguage() {
        return language == null ? defaultLanguage : language;
    }

    private YamlConfiguration extractDefault(String source) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(plugin.getResource(source))) {
            return YamlConfiguration.loadConfiguration(inputStreamReader);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to extract default file: " + source);
            if (Settings.DEBUG.toBool()) {
                e.printStackTrace();
            }
            throw new RuntimeException();
        }
    }

    public void validateConfigs() {
        settings = validate("settings.yml", defaultSettings);
        langsFolder = new File(plugin.getDataFolder(), "languages");
        langsFolder.mkdir();
        String languageFile = "langs/" + settings.getString(Settings.PLUGIN_LANGUAGE.getPath()) + ".yml";
        language = validate(languageFile, defaultLanguage);
    }

    private YamlConfiguration validate(String configName, YamlConfiguration defaultConfiguration) {
        File file = extractConfiguration(configName);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        boolean updated = false;
        for (String key : defaultConfiguration.getKeys(true)) {
            if (configuration.get(key) == null) {
                updated = true;
                plugin.getServer().getConsoleSender().sendMessage(getLanguage().getString(Messages.UPDATING_CONFIG.getPath()));
                configuration.set(key, defaultConfiguration.get(key));
            }
        }
        for (String key : configuration.getKeys(false)) {
            if (defaultConfiguration.get(key) == null) {
                updated = true;
                plugin.getServer().getConsoleSender().sendMessage(getLanguage().getString(Messages.REMOVING_CONFIG.getPath()));
                configuration.set(key, null);
            }
        }

        if (updated) {
            try {
                configuration.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save updated configuration file: " + file.getName());
                if (Settings.DEBUG.toBool()) {
                    e.printStackTrace();
                }
            }
        }
        return configuration;
    }

    public File extractConfiguration(String fileName) {
        File file = new File(this.plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
        return file;
    }
}
