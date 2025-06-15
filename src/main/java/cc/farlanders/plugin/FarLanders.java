package cc.farlanders.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import cc.farlanders.command.CommandHandler;
import cc.farlanders.command.cmds.GenerateFarLandsCommand;

public final class FarLanders extends JavaPlugin {

    private final CommandHandler handler = new CommandHandler();

    @Override
    public void onEnable() {
        getLogger().info("FarLanders enabled!");

        var farlandersCommand = getCommand("farlanders");
        if (farlandersCommand != null) {
            farlandersCommand.setExecutor(handler);
            handler.register(new GenerateFarLandsCommand());
            getLogger().info("FarLands command registered successfully!");
        } else {
            getLogger().severe("Failed to register 'farlanders' command: command not found in plugin.yml!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("FarLanders disabled!");
    }
}
