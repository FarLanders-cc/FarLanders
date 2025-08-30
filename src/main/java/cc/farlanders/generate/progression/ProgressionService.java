package cc.farlanders.generate.progression;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Small in-memory progression registry for the refactor. Persistence and player
 * linkage are future work.
 */
public final class ProgressionService {
    private static final Map<String, BiomeRequirement> REQUIREMENTS = new HashMap<>();

    private ProgressionService() {
    }

    public static void addRequirement(BiomeRequirement req) {
        REQUIREMENTS.put(req.biomeId(), req);
    }

    public static Optional<BiomeRequirement> getRequirementFor(String biomeId) {
        return Optional.ofNullable(REQUIREMENTS.get(biomeId));
    }
}
