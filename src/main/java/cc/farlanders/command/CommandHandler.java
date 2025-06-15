package cc.farlanders.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
            sender.sendMessage(Color.GREEN + "Available commands:");
            for (BaseCommand command : commands.values()) {
                if (sender.hasPermission(command.permission())) {
                    sender.sendMessage(Color.GRAY + "/" + label + " " + command.name() + Color.SILVER + " - "
                            + Color.GRAY + command.description());
                }
            }
            return true;
        }

        BaseCommand command = commands.get(args[0].toLowerCase());

        if (command == null) {
            sender.sendMessage(Color.MAROON + "Unknown subcommand: " + Color.GRAY + args[0] + "\n" + Color.GRAY
                    + "Use /" + label + " for help.");
            return true;
        }

        if (!sender.hasPermission(command.permission())) {
            sender.sendMessage(Color.MAROON + "You do not have permission to use this command.");
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        boolean success = command.execute(sender, subArgs);

        if (!success) {
            sender.sendMessage(
                    Color.MAROON + "Usage: " + Color.GRAY + "/" + label + " " + command.name() + " " + command.usage());
            if (command.description() != null && !command.description().isEmpty()) {
                sender.sendMessage(Color.GRAY + command.description());
            }
        }
        return success;
    }
}
