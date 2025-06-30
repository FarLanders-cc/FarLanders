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
        return "Teleports between worlds or to the FarLands world.";
    }

    @Override
    public String usage() {
        return "tp farlands | tp [world] [x] [y] [z]";
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

        // Check if user wants to teleport to FarLands world
        if (args[0].equalsIgnoreCase("farlands")) {
            return teleportToFarLands(player);
        }

        // Regular teleport command
        return regularTeleport(player, args);
    }

    private boolean teleportToFarLands(Player player) {
        // Look for FarLands world (default name or existing world with FarLands
        // generator)
        World farlandsWorld = findFarLandsWorld();

        if (farlandsWorld == null) {
            player.sendMessage("§cNo FarLands world found! Use §6/farlanders generate§c to create one first.");
            return false;
        }

        // Teleport to spawn or a safe location in the FarLands world
        Location spawnLocation = farlandsWorld.getSpawnLocation();
        Location safeLocation = findSafeLandingSpot(farlandsWorld, spawnLocation);

        player.teleport(safeLocation);
        player.sendMessage("§aWelcome to the FarLands world: §6" + farlandsWorld.getName() + "§a!");
        player.sendMessage("§7Coordinates: §f(" +
                (int) safeLocation.getX() + ", " + (int) safeLocation.getY() + ", " + (int) safeLocation.getZ() + ")");

        return true;
    }

    private World findFarLandsWorld() {
        // First, check for the default "farlands" world
        World defaultFarlands = Bukkit.getWorld("farlands");
        if (defaultFarlands != null) {
            return defaultFarlands;
        }

        // Look for any world that was generated with FarLandsGenerator
        for (World world : Bukkit.getWorlds()) {
            // Check if this world uses our custom generator
            if (world.getGenerator() instanceof cc.farlanders.generate.FarLandsGenerator) {
                return world;
            }
        }

        return null;
    }

    private boolean regularTeleport(Player player, String[] args) {
        String worldName = args[0];

        double x = 0;
        double y = 64;
        double z = 0;

        if (args.length >= 4) {
            try {
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid coordinates. Please enter valid numbers.");
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

    private Location findSafeLandingSpot(World world, Location target) {
        // Find the highest solid block at this location
        int highestY = world.getHighestBlockYAt((int) target.getX(), (int) target.getZ());

        // Make sure we're not spawning underground or too high
        int safeY = Math.max(highestY + 2, 70); // At least 2 blocks above ground or y=70, whichever is higher
        safeY = Math.min(safeY, 200); // But not too high

        return new Location(world, target.getX(), safeY, target.getZ());
    }
}
