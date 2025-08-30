package cc.farlanders.generate.biomes.adapter;

import cc.farlanders.generate.biomes.BiomeProvider;
import cc.farlanders.generate.biomes.api.BiomeModule;
import cc.farlanders.generate.biomes.api.BiomeRegistry;

/**
 * Adapter that registers a small set of modules which delegate to the existing
 * BiomeProvider.
 */
public final class ProviderAdapter {
    private ProviderAdapter() {
    }

    public static void registerAdapters(BiomeProvider provider) {
        // Create minimal modules that ask the provider for a biome at coords.
        BiomeModule providerModule = new BiomeModule() {
            @Override
            public String id() {
                return "provider-adapter";
            }

            @Override
            public double weight() {
                return 1.0;
            }

            @Override
            public String selectForCoords(int x, int z) {
                return provider.getBiomeAt(x, z);
            }
        };

        BiomeRegistry.register(providerModule);
    }
}
