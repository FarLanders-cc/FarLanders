package cc.farlanders.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class CommandHandler implements CommandExecutor {
    private final Map<String, BaseCommand> commands = new HashMap<>();

    public void register(BaseCommand command) {
        if (commands.containsKey(command.name())) {
            throw new IllegalArgumentException("Command with name " + command.name() + " is already registered.");
        }
        commands.put(command.name(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "Available commands:");
            for (BaseCommand command : commands.values()) {
                if (sender.hasPermission(command.permission())) {
                    sender.sendMessage(ChatColor.GRAY + "/" + label + " " + command.name() + ChatColor.DARK_GRAY + " - "
                            + ChatColor.GRAY + command.description());
                }
            }
            return true;
        }

        BaseCommand command = commands.get(args[0].toLowerCase());

        if (command == null) {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + ChatColor.GRAY + args[0] + "\n" + ChatColor.GRAY
                    + "Use /" + label + " for help.");
            return true;
        }

        if (!sender.hasPermission(command.permission())) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        boolean success = command.execute(sender, subArgs);

        if (!success) {
            sender.sendMessage(
                    ChatColor.RED + "Usage: " + ChatColor.GRAY + "/" + label + " " + command.name() + " "
                            + command.usage());
            if (command.description() != null && !command.description().isEmpty()) {
                sender.sendMessage(ChatColor.GRAY + command.description());
            }
        }
        return success;
    }
}
