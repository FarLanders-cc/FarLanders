package cc.farlanders.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cc.farlanders.command.BaseCommand;

public class TeleportFarLandsCommand implements BaseCommand {

    @Override
    public String name() {
        return "tp";
    }

    @Override
    public String description() {
        return "Teleports to a specific world and coordinates.";
    }

    @Override
    public String usage() {
        return "tp [world] <x> <y> <z>";
    }

    @Override
    public String permission() {
        return "farlanders.tp";
    }

    @Override
    public String alias() {
        return "teleport";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender != null) {
                sender.sendMessage("§cThis command can only be used by players.");
            }
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: " + usage());
            return false;
        }

        Player player = (Player) sender;
        String worldName = args[0];

        double x = 0, y = 64, z = 0;

        if (args.length >= 4) {
            try {
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid coordinates. Please enter valid numbers.");
                return false;
            }
        }

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            player.sendMessage("§cWorld '" + worldName + "' does not exist.");
            return false;
        }

        player.teleport(new Location(world, x, y, z));
        player.sendMessage("§aTeleported to " + worldName + " at (" + x + ", " + y + ", " + z + ").");

        return true;
    }
}
