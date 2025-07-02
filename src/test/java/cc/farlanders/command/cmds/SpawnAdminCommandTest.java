package cc.farlanders.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpawnAdminCommandTest {

    private SpawnAdminCommand command;

    @Mock
    private CommandSender sender;

    @Mock
    private Player player;

    @Mock
    private World world;

    @Mock
    private Server server;

    @BeforeEach
    void setUp() {
        command = new SpawnAdminCommand();
    }

    @Test
    void testCommandMetadata() {
        assertEquals("spawn", command.name());
        assertEquals("Manage world spawn settings and Far Lands spawn points", command.description());
        assertEquals("/farlanders spawn <set|reset|info|setfarlands> [world]", command.usage());
        assertEquals("farlanders.admin.spawn", command.permission());
        assertEquals("", command.alias());
    }

    @Test
    void testExecuteWithNoArgs() {
        boolean result = command.execute(sender, new String[0]);

        assertTrue(result);
        verify(sender).sendMessage("§cUsage: /farlanders spawn <set|reset|info|setfarlands> [world]");
    }

    @Test
    void testExecuteWithInvalidSubcommand() {
        boolean result = command.execute(sender, new String[] { "invalid" });

        assertTrue(result);
        verify(sender).sendMessage("§cUnknown subcommand: invalid");
        verify(sender).sendMessage("§eAvailable subcommands: set, reset, info, setfarlands");
    }

    @Test
    void testExecuteSetSubcommandAsConsole() {
        boolean result = command.execute(sender, new String[] { "set" });

        assertTrue(result);
        verify(sender).sendMessage("§cThis command can only be used by players");
    }

    @Test
    void testExecuteInfoSubcommandAsConsole() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getWorld("world")).thenReturn(world);
            when(world.getSpawnLocation()).thenReturn(new Location(world, 0, 64, 0));
            when(world.getName()).thenReturn("world");

            boolean result = command.execute(sender, new String[] { "info" });

            assertTrue(result);
            verify(sender).sendMessage(contains("=== Spawn Information for"));
        }
    }

    @Test
    void testExecuteSetFarlandsSubcommandAsConsole() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getWorld("world")).thenReturn(world);

            boolean result = command.execute(sender, new String[] { "setfarlands" });

            assertTrue(result);
            verify(sender).sendMessage("§aWorld spawn set to Far Lands location:");
        }
    }

    @Test
    void testExecuteSetSubcommandAsPlayer() {
        Location location = new Location(world, 0, 64, 0);
        when(player.getLocation()).thenReturn(location);
        when(world.getName()).thenReturn("test-world");

        boolean result = command.execute(player, new String[] { "set" });

        assertTrue(result);
        verify(world).setSpawnLocation(any(Location.class));
        verify(player).sendMessage("§aWorld spawn set to your current location:");
        verify(player).sendMessage("§eWorld: §ftest-world");
        verify(player).sendMessage("§eX: §f0 §eY: §f64 §eZ: §f0");
    }

    @Test
    void testExecuteResetSubcommandWithWorldArg() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getWorld("test-world")).thenReturn(world);

            when(world.getName()).thenReturn("test-world");
            when(world.getHighestBlockYAt(0, 0)).thenReturn(63);

            boolean result = command.execute(sender, new String[] { "reset", "test-world" });

            assertTrue(result);
            verify(world).setSpawnLocation(any(Location.class));
            verify(sender).sendMessage("§aWorld spawn reset to default location:");
            verify(sender).sendMessage("§eWorld: §ftest-world");
            verify(sender).sendMessage("§eX: §f0 §eY: §f64 §eZ: §f0");
        }
    }

    @Test
    void testExecuteResetSubcommandWithInvalidWorld() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getWorld("invalid-world")).thenReturn(null);

            boolean result = command.execute(sender, new String[] { "reset", "invalid-world" });

            assertTrue(result);
            verify(sender).sendMessage("§cWorld 'invalid-world' not found");
        }
    }

    @Test
    void testExecuteInfoSubcommandAsPlayer() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getWorld("test-world")).thenReturn(world);

            when(player.getWorld()).thenReturn(world);
            when(world.getName()).thenReturn("test-world");
            when(world.getSpawnLocation()).thenReturn(new Location(world, 10, 65, 20));

            boolean result = command.execute(player, new String[] { "info" });

            assertTrue(result);
            verify(player).sendMessage(contains("=== Spawn Information for test-world ==="));
            verify(player).sendMessage(contains("§eX: §f10"));
            verify(player).sendMessage(contains("§eY: §f65"));
            verify(player).sendMessage(contains("§eZ: §f20"));
        }
    }
}
