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

## World Generation Tests

##### 1st [`e533f58`](https://github.com/clxrityy/FarLanders/commit/e533f58ad06857d2eb4e057cd8fad8febbd44cc7)

[<img alt="1st world generation test" src="/assets/generation_tests/test_1.gif" />](./assets/generation_tests/test_1.gif)

##### 2nd [`346f7e6`](https://github.com/clxrityy/FarLanders/commit/346f7e6603b9edea8309055130ec3f208b908ef7)

> **More chaotic:**
>
> - Layered noise for multi-frequency distortion.
> - Sharp amplitude spikes in specific ranges (simulate glitching).
> - Coordinate wrapping and irrational offsets (classic Far Lands chaos).
> - Directional distortions (X vs Z behave differently at range).

[<img alt="2nd world generation test" src="/assets/generation_tests/test_2.gif" />](./assets/generation_tests/test_2.gif)

##### 3rd [`e544e44`](https://github.com/clxrityy/FarLanders/commit/e544e44d3a5089f43fb71585f27796b1ad8f8e25)

> - No water here just void land.
> - Tunnelistic structures.
> - Not enough resources
>   - Vast diamond ore veins rather than scattered and sparse.
>   - No trees, grass, or other surface features.

[<img alt="3rd world generation test" src="/assets/generation_tests/test_3.gif" />](./assets/generation_tests/test_3.gif)

##### 4th

> - More _land_ than _void_.
> - Ores can be found with appropriate amounts deeper underground.
> - Canvernish and tunnelestic.

[<img alt="4th world generation test" src="/assets/generation_tests/test_4.gif" />](./assets/generation_tests/test_4.gif)
