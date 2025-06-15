package cc.farlanders.command;

import org.bukkit.command.CommandSender;

public interface BaseCommand {
    String name();

    String description();

    String usage();

    String permission();

    String alias();

    boolean execute(CommandSender sender, String[] args);
}
