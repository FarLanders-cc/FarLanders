package cc.farlanders.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FarLandersTabCompleterTest {

    private FarLandersTabCompleter tabCompleter;

    @Mock
    private CommandSender sender;

    @Mock
    private Player player;

    @Mock
    private Command command;

    @Mock
    private Server server;

    @Mock
    private World world1;

    @Mock
    private World world2;

    @BeforeEach
    void setUp() {
        tabCompleter = new FarLandersTabCompleter();
    }

    @Test
    void testFirstArgumentCompletions() {
        when(sender.hasPermission("farlanders.tp")).thenReturn(true);
        when(sender.hasPermission("farlanders.generate")).thenReturn(true);
        when(sender.hasPermission("farlanders.admin.spawn")).thenReturn(true);

        List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders", new String[] { "" });

        assertNotNull(completions);
        assertTrue(completions.contains("tp"));
        assertTrue(completions.contains("generate"));
        assertTrue(completions.contains("spawn"));
    }

    @Test
    void testFirstArgumentCompletionsWithoutPermissions() {
        when(sender.hasPermission(anyString())).thenReturn(false);

        List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders", new String[] { "" });

        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }

    @Test
    @Disabled("WorldInfo class not available in test environment")
    void testTeleportSubcommandCompletions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getWorlds).thenReturn(List.of(world1, world2));
            when(world1.getName()).thenReturn("world");
            when(world2.getName()).thenReturn("world_nether");

            List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders",
                    new String[] { "tp", "" });

            assertNotNull(completions);
            assertTrue(completions.contains("farlands"));
            assertTrue(completions.contains("world"));
            assertTrue(completions.contains("world_nether"));
        }
    }

    @Test
    @Disabled("WorldInfo class not available in test environment")
    void testGenerateSubcommandCompletions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getWorlds).thenReturn(List.of(world1, world2));
            when(world1.getName()).thenReturn("world");
            when(world2.getName()).thenReturn("world_nether");

            List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders",
                    new String[] { "generate", "" });

            assertNotNull(completions);
            assertTrue(completions.contains("world"));
            assertTrue(completions.contains("world_nether"));
            assertTrue(completions.contains("--seed"));
            assertTrue(completions.contains("--type"));
        }
    }

    @Test
    void testSpawnSubcommandCompletions() {
        List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders",
                new String[] { "spawn", "" });

        assertNotNull(completions);
        assertTrue(completions.contains("set"));
        assertTrue(completions.contains("reset"));
        assertTrue(completions.contains("info"));
        assertTrue(completions.contains("setfarlands"));
    }

    @Test
    @Disabled("WorldInfo class not available in test environment")
    void testSpawnResetWorldCompletions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getWorlds).thenReturn(List.of(world1, world2));
            when(world1.getName()).thenReturn("world");
            when(world2.getName()).thenReturn("world_nether");

            List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders",
                    new String[] { "spawn", "reset", "" });

            assertNotNull(completions);
            assertTrue(completions.contains("world"));
            assertTrue(completions.contains("world_nether"));
        }
    }

    @Test
    void testPartialMatching() {
        when(sender.hasPermission("farlanders.tp")).thenReturn(true);
        when(sender.hasPermission("farlanders.generate")).thenReturn(true);
        when(sender.hasPermission("farlanders.admin.spawn")).thenReturn(true);

        List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders",
                new String[] { "t" });

        assertNotNull(completions);
        assertTrue(completions.contains("tp"));
        assertFalse(completions.contains("generate"));
        assertFalse(completions.contains("spawn"));
    }

    @Test
    void testUnknownSubcommand() {
        List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders",
                new String[] { "unknown", "arg" });

        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }

    @Test
    void testEmptyArgs() {
        List<String> completions = tabCompleter.onTabComplete(sender, command, "farlanders",
                new String[] {});

        assertNotNull(completions);
        // Empty args (length 0) returns empty list
        assertTrue(completions.isEmpty());
    }
}
