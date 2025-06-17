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

## Progress

#### World Generation Tests

##### 1st

<img alt="1st world generation test" src="/assets/generation_tests/test_1.gif" />

##### 2nd

> **More chaotic:**
>
> - Layered noise for multi-frequency distortion.
> - Sharp amplitude spikes in specific ranges (simulate glitching).
> - Coordinate wrapping and irrational offsets (classic Far Lands chaos).
> - Directional distortions (X vs Z behave differently at range).

<img alt="2nd world generation test" src="/assets/generation_tests/test_2.gif" />
