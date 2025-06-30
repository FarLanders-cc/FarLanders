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

        return subcommands;
    }

    private List<String> getTeleportCompletions(String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 2 -> {
                // Second argument for tp: "farlands" or world names
                completions.add("farlands");
                completions.addAll(getWorldNames());
            }
            // case 3 when !"farlands".equalsIgnoreCase(args[1]) -> {
            //     // Third argument for regular tp: X coordinate (suggest some common values)
            //     completions.addAll(Arrays.asList("0", "100", "1000", "~"));
            // }
            // case 4 when !"farlands".equalsIgnoreCase(args[1]) -> {
            //     // Fourth argument for regular tp: Y coordinate
            //     completions.addAll(Arrays.asList("64", "80", "100", "~"));
            // }
            // case 5 when !"farlands".equalsIgnoreCase(args[1]) -> {
            //     // Fifth argument for regular tp: Z coordinate
            //     completions.addAll(Arrays.asList("0", "100", "1000", "~"));
            // }
            default -> {
                /* No more completions */ }
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
}
