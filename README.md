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
- [Features](#features)

| [CONTRIBUTING](CONTRIBUTING.md) | [LICENSE](LICENSE) | [CODE_OF_CONDUCT](CODE_OF_CONDUCT.md) |
| ------------------------------- | ------------------ | ------------------------------------- |

## World Generation

The world generation is based on the Far Lands concept, which is a phenomenon in Minecraft where the terrain becomes distorted and chaotic at extreme distances from the center of the world. This plugin implements a custom world generator that creates these unique landscapes.

- The noise generation is based on [OpenSimplex2](https://github.com/KdotJPG/OpenSimplex2).

## Usage

- Build environment

  ```java
  ./gradlew buildEnvironment
  ```

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
