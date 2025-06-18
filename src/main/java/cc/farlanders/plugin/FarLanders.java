package cc.farlanders.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import cc.farlanders.command.CommandHandler;
import cc.farlanders.command.cmds.GenerateFarLandsCommand;
import cc.farlanders.command.cmds.TeleportFarLandsCommand;
import cc.farlanders.config.ConfigManager;

public final class FarLanders extends JavaPlugin {

    private final CommandHandler handler = new CommandHandler();

    @Override
    public void onEnable() {
        getLogger().info("FarLanders enabled!");

        ConfigManager.setup(this);

        var farlandersCommand = getCommand("farlanders");
        if (farlandersCommand != null) {
            farlandersCommand.setExecutor(handler);
            handler.register(new GenerateFarLandsCommand());
            handler.register(new TeleportFarLandsCommand());
            getLogger().info("FarLanders commands registered successfully!");
        } else {
            getLogger().severe("Failed to register 'farlanders' command: command not found in plugin.yml!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("FarLanders disabled!");
    }

    public CommandHandler getCommandHandler() {
        return handler;
    }
}
