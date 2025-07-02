package cc.farlanders.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class FarLandersTabCompleter implements TabCompleter {

    private static final String RESET_SUBCOMMAND = "reset";
    private static final String INFO_SUBCOMMAND = "info";
    private static final String SET_SUBCOMMAND = "set";
    private static final String SETFARLANDS_SUBCOMMAND = "setfarlands";

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: suggest subcommands
            completions.addAll(getAvailableSubcommands(sender));
        } else if (args.length > 1) {
            // Handle specific subcommand completions
            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "tp", "teleport" -> completions.addAll(getTeleportCompletions(args));
                case "generate" -> completions.addAll(getGenerateCompletions(args));
                case "spawn" -> completions.addAll(getSpawnCompletions(args));
                default -> {
                    /* No additional completions */ }
            }
        }

        // Filter completions based on what the user has typed
        String currentArg = args.length > 0 ? args[args.length - 1].toLowerCase() : "";
        return completions.stream()
                .filter(completion -> completion.toLowerCase().startsWith(currentArg))
                .sorted()
                .toList();
    }

    private List<String> getAvailableSubcommands(CommandSender sender) {
        List<String> subcommands = new ArrayList<>();

        // Add subcommands based on permissions
        if (sender.hasPermission("farlanders.tp")) {
            subcommands.add("tp");
        }
        if (sender.hasPermission("farlanders.generate")) {
            subcommands.add("generate");
        }
        if (sender.hasPermission("farlanders.admin.spawn")) {
            subcommands.add("spawn");
        }

        return subcommands;
    }

    private List<String> getTeleportCompletions(String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            // Second argument for tp: "farlands" or world names
            completions.add("farlands");
            completions.addAll(getWorldNames());
        }

        return completions;
    }

    private List<String> getGenerateCompletions(String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            // Second argument for generate: world names or options
            completions.addAll(getWorldNames());
            completions.addAll(Arrays.asList("--seed", "--type"));
        }

        return completions;
    }

    private List<String> getWorldNames() {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .toList();
    }

    private List<String> getSpawnCompletions(String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            // Second argument for spawn: subcommands
            completions
                    .addAll(Arrays.asList(SET_SUBCOMMAND, RESET_SUBCOMMAND, INFO_SUBCOMMAND, SETFARLANDS_SUBCOMMAND));
        } else if (args.length == 3) {
            // Third argument for spawn: world names (for reset, info, setfarlands)
            String subcommand = args[1].toLowerCase();
            if (RESET_SUBCOMMAND.equals(subcommand) || INFO_SUBCOMMAND.equals(subcommand)
                    || SETFARLANDS_SUBCOMMAND.equals(subcommand)) {
                completions.addAll(getWorldNames());
            }
        }

        return completions;
    }
}
