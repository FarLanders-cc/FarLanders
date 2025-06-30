# FarLanders

```yml
▗▄▄▄▖ ▗▄▖ ▗▄▄▖ ▗▖    ▗▄▖ ▗▖  ▗▖▗▄▄▄ ▗▄▄▄▖▗▄▄▖  ▗▄▄▖
▐▌   ▐▌ ▐▌▐▌ ▐▌▐▌   ▐▌ ▐▌▐▛▚▖▐▌▐▌  █▐▌   ▐▌ ▐▌▐▌
▐▛▀▀▘▐▛▀▜▌▐▛▀▚▖▐▌   ▐▛▀▜▌▐▌ ▝▜▌▐▌  █▐▛▀▀▘▐▛▀▚▖ ▝▀▚▖
▐▌   ▐▌ ▐▌▐▌ ▐▌▐▙▄▄▖▐▌ ▐▌▐▌  ▐▌▐▙▄▄▀▐▙▄▄▖▐▌ ▐▌▗▄▄▞▘

- # Open source project
- farlanders.cc
```

FarLanders is the primary plugin for a Minecraft server that has a unique world generation feature, allowing players to explore the Far Lands.

---

- [World Generation](#world-generation)
- [Usage](#usage)
- [World Generation Tests](#world-generation-tests)
- [Features](#features)

| [CONTRIBUTING](CONTRIBUTING.md) | [LICENSE](LICENSE) | [CODE_OF_CONDUCT](CODE_OF_CONDUCT.md) |
| ------------------------------- | ------------------ | ------------------------------------- |

## World Generation

The world generation is based on the Far Lands concept, which is a phenomenon in Minecraft where the terrain becomes distorted and chaotic at extreme distances from the center of the world. This plugin implements a custom world generator that creates these unique landscapes.

- The noise generation is based on [OpenSimplex2](https://github.com/KdotJPG/OpenSimplex2).

## Usage

- Build plugin
  ```java
  ./gradlew build
  ```
- Clean build
  ```java
  ./gradlew clean build
  ```
- Test
  ```java
  ./gradlew test
  ```

---

## [World Generation Tests](assets/generation_tests/README.md)

> - **It's actually just a log of images**

## Features

- **Beautiful Terrain Generation**: Realistic landscapes with proper biome transitions
- **Enhanced FarLands**: Classic "staticy" FarLands distortion at extreme distances (12.5M+ blocks)
- **Rich Cave Systems**: Multi-scale interconnected cave networks with natural formations
- **Proper Resource Distribution**: Realistic ore placement and biome-appropriate materials
- **Elevated Terrain**: Optimized land generation to ensure most terrain stays above sea level
- **Chaotic Biomes**: Special FarLands biomes with unique characteristics
- **Advanced Vegetation**: Biome-appropriate trees, plants, and surface features
- **Rare Structures**: Scattered structures and legendary formations for exploration
  > [Generation Architecture](GENERATION_ARCHITECTURE.md)
