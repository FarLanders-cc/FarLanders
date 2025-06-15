package cc.farlanders.plugin;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public final class FarLanders extends JavaPlugin {

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public void onEnable() {
        getLogger().info("FarLanders enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FarLanders disabled!");
    }
}
