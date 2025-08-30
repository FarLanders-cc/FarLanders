package cc.farlanders.generate.biomes.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Simple runtime registry for biome modules. Made small to keep compilation
 * safe.
 */
public final class BiomeRegistry {
    private static final List<BiomeModule> MODULES = new ArrayList<>();
    private static final Random RANDOM = new Random();

    private BiomeRegistry() {
    }

    public static void register(BiomeModule module) {
        MODULES.add(module);
        module.register();
    }

    public static List<BiomeModule> list() {
        return Collections.unmodifiableList(MODULES);
    }

    public static Optional<BiomeModule> getById(String id) {
        return MODULES.stream().filter(m -> m.id().equalsIgnoreCase(id)).findFirst();
    }

    public static Optional<BiomeModule> selectByWeight(int x, int z) {
        // First ask modules if any has a deterministic selection for these coords
        for (BiomeModule m : MODULES) {
            String s = m.selectForCoords(x, z);
            if (s != null && s.equalsIgnoreCase(m.id())) {
                return Optional.of(m);
            }
        }

        // Weighted random selection
        double total = MODULES.stream().mapToDouble(BiomeModule::weight).sum();
        if (total <= 0 || MODULES.isEmpty())
            return Optional.empty();
        double pick = RANDOM.nextDouble() * total;
        double acc = 0.0;
        for (BiomeModule m : MODULES) {
            acc += m.weight();
            if (pick <= acc)
                return Optional.of(m);
        }
        return Optional.of(MODULES.get(0));
    }
}
