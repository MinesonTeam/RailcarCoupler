package kz.hxncus.mc.railcarcoupler.config;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@Getter
public enum Messages {
    PREFIX("general.prefix"), UPDATING_CONFIG("config.updating"), REMOVING_CONFIG("config.removing"), MATERIAL_NOT_FOUND("error.material_not_found"),
    VEHICLE_SUCCESSFULLY_UNCOUPLED("general.vehicle_successfully_uncoupled"), CHOSEN_FIRST_VEHICLE("general.chosen_first_vehicle"),
    VEHICLES_SUCCESSFULLY_COUPLED("general.vehicles_successfully_coupled"), FIRST_VEHICLE_NOT_FOUND("general.first_vehicle_not_found"),
    TRAIN_CONTROL_INV_TITLE("general.train_control_inv_title");

    private final String path;

    Messages(final String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return RailcarCoupler.get().getConfigManager().getLanguage().getString(path);
    }

    public void send(@NonNull final CommandSender sender, final Object... args) {
        String message = RailcarCoupler.get().getConfigManager().getLanguage().getString(path);
        if (message == null || message.isEmpty()) {
            return;
        }
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i].toString());
        }
        sender.sendMessage(message);
    }

    public void log(final Object... args) {
        send(Bukkit.getConsoleSender(), args);
    }
}
