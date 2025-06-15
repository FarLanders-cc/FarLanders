package cc.farlanders.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

class GenerateFarLandsCommandTest {

    private GenerateFarLandsCommand command;
    private CommandSender sender;

    @BeforeEach
    void setup() {
        command = new GenerateFarLandsCommand();
        // Mock or create a CommandSender instance as needed for testing
        sender = mock(CommandSender.class);
    }

    @Test
    void testExecuteWithNoArgs() {
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            World mockWorld = mock(World.class);
            mockedBukkit.when(() -> Bukkit.createWorld(any(WorldCreator.class))).thenReturn(mockWorld);

            boolean result = command.execute(sender, new String[] {});

            assertTrue(result);
        }
    }
}
