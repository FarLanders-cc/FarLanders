package cc.farlanders.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;

import cc.farlanders.command.BaseCommand;
import cc.farlanders.plugin.FarLandsChunkGenerator;

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
        return "farlands.generate";
    }

    @Override
    public String alias() {
        return "gen";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String worldName = args.length > 0 ? args[0] : "farlands";
        sender.sendMessage(Color.AQUA + "Generating the Far Lands world: " + Color.GREEN + worldName);

        WorldCreator creator = new WorldCreator(worldName);
        creator.generator(new FarLandsChunkGenerator());

        World world = Bukkit.createWorld(creator);

        if (world != null) {
            sender.sendMessage(Color.GREEN + "Far Lands world generated successfully: " + Color.AQUA + world.getName());
        } else {
            sender.sendMessage(Color.RED + "Failed to generate the Far Lands world.");
        }

        return true;
    }
}
