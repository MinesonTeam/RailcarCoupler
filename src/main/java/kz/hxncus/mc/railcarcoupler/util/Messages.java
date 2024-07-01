package kz.hxncus.mc.railcarcoupler.util;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum Messages {
    PREFIX, VEHICLE_SUCCESSFULLY_UNCOUPLED, CHOSEN_FIRST_VEHICLE, VEHICLES_SUCCESSFULLY_COUPLED, FIRST_VEHICLE_NOT_FOUND, TRAIN_CONTROL_INV_TITLE;

    private List<String> messageList;
    private final FileConfiguration langConfig = RailcarCoupler.getInstance().getFileManager().getLangConfig();

    Messages() {
        updateMessage();
    }

    public void updateMessage() {
        Object val = langConfig.get(name().toLowerCase(Locale.ROOT), "");
        if (val instanceof List<?>) {
            messageList = ((List<?>) val).stream().map(Object::toString).collect(Collectors.toList());
        } else {
            messageList = Collections.singletonList(val.toString());
        }
        if (messageList.get(0).isEmpty()) {
            RailcarCoupler.getInstance().getLogger().severe(() -> "Message not found: " + name().toLowerCase(Locale.ROOT));
        }
    }

    public String getMessage(int index, Object... args) {
        return messageList.get(index) == null ? "" : getReplacedMessage(messageList.get(index), args);
    }

    public String getReplacedMessage(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i].toString());
        }
        return message.replace("{PREFIX}", PREFIX.messageList.get(0));
    }

    public void sendMessage(CommandSender sender, Object... args) {
        sender.sendMessage(getReplacedMessage(getMessage(0), args));
    }

    public void sendMessages(CommandSender sender, Object... args) {
        for (String message : messageList) {
            sender.sendMessage(getReplacedMessage(message, args));
        }
    }

    public static void updateAllMessages() {
        for (Messages messages : values()) {
            messages.updateMessage();
        }
    }
}
