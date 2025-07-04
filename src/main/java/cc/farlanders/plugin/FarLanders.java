package cc.farlanders.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import cc.farlanders.command.CommandHandler;
import cc.farlanders.command.FarLandersTabCompleter;
import cc.farlanders.command.cmds.GenerateFarLandsCommand;
import cc.farlanders.command.cmds.TeleportFarLandsCommand;
import cc.farlanders.compat.VersionCompatibilityManager;
import cc.farlanders.generate.config.GenerationConfig;

public final class FarLanders extends JavaPlugin {

    private final CommandHandler handler = new CommandHandler();

    @Override
    public void onEnable() {
        getLogger().info("FarLanders enabled!");

        // Initialize configuration system
        GenerationConfig.initialize(this);

        getLogger().info("Configuration loaded successfully!");

        var farlandersCommand = getCommand("farlanders");
        if (farlandersCommand != null) {
            farlandersCommand.setExecutor(handler);
            farlandersCommand.setTabCompleter(new FarLandersTabCompleter());
            handler.register(new GenerateFarLandsCommand());
            handler.register(new TeleportFarLandsCommand());
            handler.register(new cc.farlanders.command.cmds.SpawnAdminCommand());
            handler.register(new cc.farlanders.command.cmds.VersionInfoCommand());
            getLogger().info("FarLanders commands registered successfully!");
        } else {
            getLogger().severe("Failed to register 'farlanders' command: command not found in plugin.yml!");
        }

        // Optional: Log version compatibility status
        if (VersionCompatibilityManager.isViaVersionAvailable()) {
            getLogger().info("ViaVersion detected - multi-version support enabled");
        } else {
            getLogger().info("ViaVersion not found - using native version support only");
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
