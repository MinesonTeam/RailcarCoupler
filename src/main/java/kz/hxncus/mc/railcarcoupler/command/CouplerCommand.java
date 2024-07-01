package kz.hxncus.mc.railcarcoupler.command;

import kz.hxncus.mc.railcarcoupler.RailcarCoupler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CouplerCommand extends AbstractCommand {
    public CouplerCommand(RailcarCoupler plugin) {
        super(plugin, "coupler");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {

    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String... args) {
        return Collections.emptyList();
    }
}
