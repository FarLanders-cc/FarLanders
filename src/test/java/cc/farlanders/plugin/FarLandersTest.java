package cc.farlanders.plugin;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class FarLandersTest {

    private FarLanders plugin = new FarLanders();
    private static final Logger logger = mock(Logger.class);

    @BeforeEach
    void setUp() {
        logger.setLevel(java.util.logging.Level.INFO);
        doReturn(logger).when(plugin).getLogger();
        plugin.onEnable(); // Call onEnable to set up the logger
    }

    @Test
    void testOnEnable() {
        plugin.onEnable();
        verify(logger).info("FarLanders enabled!");
    }

    @Test
    void testOnDisable() {
        plugin.onDisable();
        verify(logger).info("FarLanders disabled!");
    }
}