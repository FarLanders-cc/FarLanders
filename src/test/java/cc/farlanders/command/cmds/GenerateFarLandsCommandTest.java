package cc.farlanders.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class GenerateFarLandsCommandTest {

    private GenerateFarLandsCommand command;

    @BeforeEach
    void setup() {
        command = new GenerateFarLandsCommand();
    }

    @Test
    void testExecute_WorldAlreadyExists() {
        CommandSender sender = mock(CommandSender.class);
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            World mockWorld = mock(World.class);
            mockedBukkit.when(() -> Bukkit.getWorld(any(String.class))).thenReturn(mockWorld);

            boolean result = command.execute(sender, new String[] { "existingWorld" });

            assertFalse(result);
            verify(sender).sendMessage(contains("already exists"));
        }
    }

    // @Test
    // void testExecute_WorldCreated_SenderIsPlayer() {
    // Player player = mock(Player.class);
    // World mockWorld = mock(World.class);
    // when(mockWorld.getName()).thenReturn("farlands");
    // when(mockWorld.getSpawnLocation()).thenReturn(null); // or mock a Location

    // try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
    // mockedBukkit.when(() -> Bukkit.getWorld(any(String.class))).thenReturn(null);
    // mockedBukkit.when(() ->
    // Bukkit.createWorld(any(WorldCreator.class))).thenReturn(mockWorld);

    // boolean result = command.execute(player, new String[] {});

    // assertTrue(result);
    // verify(player).sendMessage(contains("Far Lands world generated
    // successfully"));
    // verify(player).teleport((org.bukkit.Location) isNull());
    // verify(player).sendMessage(contains("teleported to the spawn point"));
    // }
    // }

    // @Test
    // void testExecute_WorldCreated_SenderIsNotPlayer() {
    // CommandSender sender = mock(CommandSender.class);
    // World mockWorld = mock(World.class);
    // when(mockWorld.getName()).thenReturn("farlands");
    // when(mockWorld.getSpawnLocation()).thenReturn(null);

    // try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
    // mockedBukkit.when(() -> Bukkit.getWorld(any(String.class))).thenReturn(null);
    // mockedBukkit.when(() ->
    // Bukkit.createWorld(any(WorldCreator.class))).thenReturn(mockWorld);

    // boolean result = command.execute(sender, new String[] {});

    // assertTrue(result);
    // verify(sender).sendMessage(contains("Far Lands world generated
    // successfully"));
    // // No teleport should be attempted
    // }
    // }

    // @Test
    // void testExecute_WorldCreationFails() {
    // CommandSender sender = mock(CommandSender.class);

    // try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
    // mockedBukkit.when(() -> Bukkit.getWorld(any(String.class))).thenReturn(null);
    // mockedBukkit.when(() ->
    // Bukkit.createWorld(any(WorldCreator.class))).thenReturn(null);

    // boolean result = command.execute(sender, new String[] {});

    // assertTrue(result);
    // verify(sender).sendMessage(contains("Failed to generate the Far Lands
    // world"));
    // }
    // }
}