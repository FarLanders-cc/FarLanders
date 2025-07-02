package cc.farlanders.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cc.farlanders.command.BaseCommand;

/**
 * Admin command for managing world spawn settings
 */
public class SpawnAdminCommand implements BaseCommand {

    @Override
    public String name() {
        return "spawn";
    }

    @Override
    public String description() {
        return "Manage world spawn settings and Far Lands spawn points";
    }

    @Override
    public String usage() {
        return "/farlanders spawn <set|reset|info|setfarlands> [world]";
    }

    @Override
    public String permission() {
        return "farlanders.admin.spawn";
    }

    @Override
    public String alias() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUsage: " + usage());
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set":
                return handleSetSpawn(sender, args);
            case "reset":
                return handleResetSpawn(sender, args);
            case "info":
                return handleSpawnInfo(sender, args);
            case "setfarlands":
                return handleSetFarLandsSpawn(sender, args);
            default:
                sender.sendMessage("§cUnknown subcommand: " + subCommand);
                sender.sendMessage("§eAvailable subcommands: set, reset, info, setfarlands");
                return true;
        }
    }

    /**
     * Set world spawn to current location
     */
    private boolean handleSetSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();
        World world = location.getWorld();

        if (world == null) {
            sender.sendMessage("§cCannot determine world from your current location");
            return true;
        }

        // Set world spawn
        world.setSpawnLocation(location);

        sender.sendMessage("§aWorld spawn set to your current location:");
        sender.sendMessage("§eWorld: §f" + world.getName());
        sender.sendMessage("§eX: §f" + location.getBlockX() + " §eY: §f" + location.getBlockY() + " §eZ: §f"
                + location.getBlockZ());

        return true;
    }

    /**
     * Reset world spawn to default (0, highest block, 0)
     */
    private boolean handleResetSpawn(CommandSender sender, String[] args) {
        String worldName = "world"; // Default world

        if (args.length > 1) {
            worldName = args[1];
        } else if (sender instanceof Player) {
            worldName = ((Player) sender).getWorld().getName();
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage("§cWorld '" + worldName + "' not found");
            return true;
        }

        // Reset to default spawn (0, highest block, 0)
        int highestY = world.getHighestBlockYAt(0, 0) + 1;
        Location defaultSpawn = new Location(world, 0.5, highestY, 0.5);
        world.setSpawnLocation(defaultSpawn);

        sender.sendMessage("§aWorld spawn reset to default location:");
        sender.sendMessage("§eWorld: §f" + world.getName());
        sender.sendMessage("§eX: §f0 §eY: §f" + highestY + " §eZ: §f0");

        return true;
    }

    /**
     * Show current spawn information
     */
    private boolean handleSpawnInfo(CommandSender sender, String[] args) {
        String worldName = "world"; // Default world

        if (args.length > 1) {
            worldName = args[1];
        } else if (sender instanceof Player) {
            worldName = ((Player) sender).getWorld().getName();
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage("§cWorld '" + worldName + "' not found");
            return true;
        }

        Location spawn = world.getSpawnLocation();
        sender.sendMessage("§6=== Spawn Information for " + world.getName() + " ===");
        sender.sendMessage("§eX: §f" + spawn.getBlockX());
        sender.sendMessage("§eY: §f" + spawn.getBlockY());
        sender.sendMessage("§eZ: §f" + spawn.getBlockZ());
        sender.sendMessage("§eDistance from origin: §f"
                + Math.round(Math.sqrt(spawn.getX() * spawn.getX() + spawn.getZ() * spawn.getZ())) + " blocks");

        return true;
    }

    /**
     * Set spawn to a location in the Far Lands
     */
    private boolean handleSetFarLandsSpawn(CommandSender sender, String[] args) {
        String worldName = "world"; // Default world

        if (args.length > 1) {
            worldName = args[1];
        } else if (sender instanceof Player) {
            worldName = ((Player) sender).getWorld().getName();
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage("§cWorld '" + worldName + "' not found");
            return true;
        }

        // Set spawn to Far Lands coordinates (12,550,000+ blocks out)
        // This is safely in the Far Lands territory
        int farLandsX = 12550000;
        int farLandsZ = 100; // Keep Z reasonable for initial spawn
        int highestY = world.getHighestBlockYAt(farLandsX, farLandsZ) + 1;

        // Ensure Y is reasonable (not void or too high)
        if (highestY < 10) {
            highestY = 64; // Fallback to sea level
        } else if (highestY > 200) {
            highestY = 100; // Prevent spawning too high
        }

        Location farLandsSpawn = new Location(world, farLandsX + 0.5, highestY, farLandsZ + 0.5);
        world.setSpawnLocation(farLandsSpawn);

        sender.sendMessage("§aWorld spawn set to Far Lands location:");
        sender.sendMessage("§eWorld: §f" + world.getName());
        sender.sendMessage("§eX: §f" + farLandsX + " §eY: §f" + highestY + " §eZ: §f" + farLandsZ);
        sender.sendMessage(
                "§6Note: §eThis location is " + (farLandsX / 1000) + "k blocks from origin - deep in the Far Lands!");
        sender.sendMessage("§eNew players will spawn at this location in the Far Lands.");

        return true;
    }
}
