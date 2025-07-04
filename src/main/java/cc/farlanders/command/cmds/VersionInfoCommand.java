package cc.farlanders.command.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cc.farlanders.command.BaseCommand;
import cc.farlanders.compat.VersionCompatibilityManager;
import cc.farlanders.plugin.FarLanders;

public class VersionInfoCommand implements BaseCommand {

    @Override
    public String name() {
        return "version";
    }

    @Override
    public String description() {
        return "Display version compatibility information";
    }

    @Override
    public String usage() {
        return "version";
    }

    @Override
    public String permission() {
        return "farlanders.version";
    }

    @Override
    public String alias() {
        return "ver";
    }

    // ...existing code...

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender == null) {
            return false;
        }

        FarLanders plugin = JavaPlugin.getPlugin(FarLanders.class);

        sendBasicInfo(sender, plugin);
        sendVersionSupportInfo(sender);

        // If the sender is a player, show their version info
        if (sender instanceof Player player) {
            sendPlayerVersionInfo(sender, player);
        }

        sender.sendMessage(ChatColor.GOLD + "======================================");
        return true;
    }

    private void sendBasicInfo(CommandSender sender, FarLanders plugin) {
        sender.sendMessage(ChatColor.GOLD + "=== FarLanders Version Information ===");
        sender.sendMessage(
                ChatColor.YELLOW + "Plugin Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Server Version: " + ChatColor.WHITE + org.bukkit.Bukkit.getVersion());
        sender.sendMessage(
                ChatColor.YELLOW + "Bukkit Version: " + ChatColor.WHITE + org.bukkit.Bukkit.getBukkitVersion());
    }

    private void sendVersionSupportInfo(CommandSender sender) {
        if (VersionCompatibilityManager.isViaVersionAvailable()) {
            sender.sendMessage(ChatColor.GREEN + "✓ Multi-version support: " + ChatColor.WHITE + "Available");
            sender.sendMessage(ChatColor.GREEN + "✓ ViaVersion: " + ChatColor.WHITE + "Enabled");

            if (VersionCompatibilityManager.isViaRewindAvailable()) {
                sender.sendMessage(ChatColor.GREEN + "✓ ViaRewind: " + ChatColor.WHITE + "Enabled");
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "⚠ Multi-version support: " + ChatColor.WHITE + "Not available");
            sender.sendMessage(ChatColor.GRAY + "  Install ViaVersion for enhanced compatibility");
        }
    }

    private void sendPlayerVersionInfo(CommandSender sender, Player player) {
        int protocolVersion = VersionCompatibilityManager.getPlayerProtocolVersion(player);
        if (protocolVersion == -1) {
            return;
        }

        String versionName = VersionCompatibilityManager.getVersionString(protocolVersion);
        sender.sendMessage(ChatColor.AQUA + "Your Client Version: " + ChatColor.WHITE + versionName +
                ChatColor.GRAY + " (Protocol " + protocolVersion + ")");

        // Check compatibility with common versions
        if (protocolVersion >= 763) { // 1.20+
            sender.sendMessage(ChatColor.GREEN + "✓ Modern features supported");
        } else if (protocolVersion >= 754) { // 1.16.4+
            sender.sendMessage(ChatColor.YELLOW + "⚠ Some modern features may be limited");
        } else {
            sender.sendMessage(ChatColor.RED + "⚠ Legacy client - some features may not work correctly");
        }
    }
}
