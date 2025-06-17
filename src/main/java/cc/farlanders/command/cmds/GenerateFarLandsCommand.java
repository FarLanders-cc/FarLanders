package cc.farlanders.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cc.farlanders.command.BaseCommand;
import cc.farlanders.generate.FarLandsGenerator;

public class GenerateFarLandsCommand implements BaseCommand {

    @Override
    public String name() {
        return "generate";
    }

    @Override
    public String description() {
        return "Generates the Far Lands world.";
    }

    @Override
    public String usage() {
        return "generate [worldName]";
    }

    @Override
    public String permission() {
        return "farlanders.command";
    }

    @Override
    public String alias() {
        return "gen";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String worldName = args.length > 0 ? args[0] : "farlands";
        sender.sendMessage("§bGenerating the Far Lands world: §a" + worldName);

        // Check if the world already exists
        if (Bukkit.getWorld(worldName) != null) {
            sender.sendMessage("§cA world with that name already exists.");
            return false;
        }

        WorldCreator creator = new WorldCreator(worldName);
        creator.generator(new FarLandsGenerator(creator.seed()));

        World world = Bukkit.createWorld(creator);

        if (world != null) {
            sender.sendMessage("§aFar Lands world generated successfully: §3" + world.getName());

            // Teleport the sender to the spawn point of the new world
            if (sender instanceof Player player) {
                player.teleport(world.getSpawnLocation());
                player.sendMessage("§eYou have been teleported to the spawn point of the Far Lands world.");
            }
        } else {
            sender.sendMessage("§cFailed to generate the Far Lands world.");
        }

        return true;
    }
}
