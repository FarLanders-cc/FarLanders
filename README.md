# FarLanders <img src="logo.png" alt="logo" width="32" />

```yml
▗▄▄▄▖ ▗▄▖ ▗▄▄▖ ▗▖    ▗▄▖ ▗▖  ▗▖▗▄▄▄ ▗▄▄▄▖▗▄▄▖  ▗▄▄▖
▐▌   ▐▌ ▐▌▐▌ ▐▌▐▌   ▐▌ ▐▌▐▛▚▖▐▌▐▌  █▐▌   ▐▌ ▐▌▐▌
▐▛▀▀▘▐▛▀▜▌▐▛▀▚▖▐▌   ▐▛▀▜▌▐▌ ▝▜▌▐▌  █▐▛▀▀▘▐▛▀▚▖ ▝▀▚▖
▐▌   ▐▌ ▐▌▐▌ ▐▌▐▙▄▄▖▐▌ ▐▌▐▌  ▐▌▐▙▄▄▀▐▙▄▄▖▐▌ ▐▌▗▄▄▞▘

- # Open source project
- farlanders.cc
```

FarLanders is the primary plugin for a Minecraft server that features **enhanced world generation** for exploring the legendary Far Lands. Experience progressively more distorted terrain as you journey to the world's edge and discover the unique landscape that awaits.

---

- [World Generation Overview](#-world-generation-overview)
- [Usage](#usage)
- [World Generation Tests](#world-generation-tests)
- [Features](#features)
- [Commands](#commands)

| [CONTRIBUTING](CONTRIBUTING.md) | [LICENSE](LICENSE) | [CODE_OF_CONDUCT](CODE_OF_CONDUCT.md) |
| ------------------------------- | ------------------ | ------------------------------------- |

## 🌟 **World Generation Overview**

Experience **enhanced terrain generation** where distance from spawn determines terrain complexity and distortion:

- **📍 0-100k blocks**: Normal terrain with standard generation
- **🏗️ 100k-1M blocks**: Subtle terrain variations and unique structures appear
- **⚡ 1M-12.5M blocks**: Advanced generation with floating islands and terrain distortions
- **🌀 12.5M+ blocks**: The true Far Lands - chaotic, unique terrain generation

### **Enhanced Generation Features**

Discover advanced world generation including:

- **Custom terrain generation** with increasing complexity
- **Unique biome variants** as you approach the Far Lands
- **Advanced structure generation** throughout the journey
- **Progressive terrain distortion** leading to the Far Lands

---

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

---

## Features

### **� World Generation & Exploration**

- **Enhanced Terrain Generation**: Advanced landscapes with proper biome transitions
- **Progressive Complexity**: Terrain becomes more complex and distorted with distance
- **Unique Far Lands**: Experience classic "chaotic" Far Lands distortion at extreme distances (12.5M+ blocks)
- **Advanced Structures**: Discover unique formations and landmarks throughout your journey
- **Distance-Based Features**: Unlock new terrain types and generation patterns as you explore

### **🌍 World Generation**

- **Beautiful Terrain Generation**: Realistic landscapes with proper biome transitions
- **Enhanced FarLands**: Classic "staticy" FarLands distortion at extreme distances (12.5M+ blocks)
- **Rich Cave Systems**: Multi-scale interconnected cave networks with natural formations
- **Proper Resource Distribution**: Realistic ore placement and biome-appropriate materials
- **Elevated Terrain**: Optimized land generation to ensure most terrain stays above sea level
- **Chaotic Biomes**: Special FarLands biomes with unique characteristics
- **Advanced Vegetation**: Biome-appropriate trees, plants, and surface features
- **Rare Structures**: Scattered structures and legendary formations for exploration
- **Floating Sky Islands**: Rare high-altitude islands containing valuable rare ores
- **Rich Agriculture**: Biome-specific farms, crops, and agricultural infrastructure
- **Enhanced Mob Spawning**: Specialized habitats and environments for passive mobs
  > [Generation Architecture](GENERATION_ARCHITECTURE.md)

---

## Commands

### `/farlanders tp farlands`

Teleports you to the dedicated FarLands world. This command automatically finds and teleports you to an existing FarLands world.

**Usage:**

- `/farlanders tp farlands` - Teleport to the FarLands world

**Notes:**

- The command will find any world generated with the FarLands generator
- If no FarLands world exists, it will prompt you to create one using `/farlanders generate`
- You'll be teleported to a safe location in the FarLands world

### `/farlanders tp [world] <x> <y> <z>`

General teleport command for specific coordinates.

### `/farlanders generate`

Generates the FarLands world using the enhanced generation system.

**Permissions:**

- `farlanders.tp` - Required for teleport commands (default: op)
- `farlanders.generate` - Required for world generation (default: op)

---

## Tab Completion

The FarLanders plugin includes comprehensive tab completion for all commands:

### **Auto-completion Features:**

- **Subcommands**: Press `Tab` after `/farlanders` to see available commands (`tp`, `generate`)
- **World Names**: Automatically suggests available world names
- **Directions**: Complete direction names for FarLands teleportation
- **Coordinates**: Suggests common coordinate values for manual teleportation
- **Permission-based**: Only shows commands you have permission to use

### **Tab Completion Examples:**

```
/farlanders [TAB]          → tp, generate
/farlanders tp [TAB]       → farlands, world_name, world_nether
/farlanders tp farlands [TAB] → (teleports to FarLands world)
/farlanders tp world [TAB] → 0, 100, 1000, ~
```

### **Smart Context Awareness:**

- Recognizes when you're using `farlands` vs regular teleport
- Suggests appropriate options based on command position
- Filters suggestions based on what you've already typed

---

## 🌍 Enhanced World Generation Features

### **Floating Sky Islands** ⛅

- **Location**: High altitude (Y 200-280)
- **Rarity**: Very rare - approximately 1 every 512x512 blocks
- **Special Materials**:
  - **Netherite Blocks** (Extremely rare)
  - **Ancient Debris** (Very rare)
  - **Diamond Ore** (Rare)
  - **Emerald Ore** (Rare)
  - **Gold, Lapis, and other valuable ores**
- **Structure**: Multi-layered islands with ethereal materials at top (End Stone, Calcite) and solid foundations (Obsidian, Blackstone)

### **Enhanced Mob Spawning** 🐄🐎🐺

- **Biome-Specific Environments**: Each biome generates suitable habitats for passive mobs
- **Grazing Areas**: Open spaces with grass for cows, sheep, and horses
- **Water Sources**: Ponds and streams for animal hydration
- **Forest Clearings**: Open areas in forests for wolves and foxes
- **Desert Oases**: Rare water sources in deserts for camels
- **Specialized Habitats**:
  - Wolf dens in taiga
  - Parrot perches in jungle
  - Lily pad areas for frogs in swamps
  - Mooshroom grazing areas in mushroom fields

### **Rich Agriculture System** 🌾🍅🌽

- **Biome-Appropriate Farms**: Different crop types based on local climate
- **Plains**: Large wheat fields, vegetable gardens, and scarecrows
- **Forest**: Berry farms, mushroom cultivation, and fruit orchards
- **Desert**: Oasis farms with irrigation and cactus plantations
- **Savanna**: Acacia tree farms and managed grasslands
- **Taiga**: Protected greenhouses for cold-climate growing
- **Jungle**: Cocoa plantations, bamboo farms, and melon patches
- **Swamp**: Rice paddies and kelp farms
- **Infrastructure**:
  - Fencing around farms
  - Irrigation systems
  - Storage chests and composters
  - Farm paths and walkways

### **Enhanced Biome Architecture** 🏗️

- **Flower Forest**: More diverse vegetation and agricultural potential
- **Meadow**: Ideal grazing areas for livestock
- **Improved Transitions**: Smoother blending between different biomes
- **Specialized Structures**: Biome-specific buildings and formations

### **Ancient Buried Ruins** ⛪

- **Location**: Deep underground, 10-25 blocks below the surface
- **Rarity**: Extremely rare - only appears far from spawn (1000+ blocks)
- **Structures**:
  - **Temple Ruins**: Religious structures with clerical villagers and ceremonial chambers
  - **Library Ruins**: Knowledge centers with librarian villagers and enchanted books
  - **Marketplace Ruins**: Trading posts with merchant villagers and storage areas
  - **Fortress Ruins**: Military outposts with weaponsmith villagers and armories
