# NoEscape

An original atmospheric horror mod for Minecraft Forge 1.20.1. Something is
always just out of sight. There is no escape.

## What it does (all in-game, nothing else)
- **Whispers** — occasional action-bar text implying you're being watched.
- **Ambient sounds** — unsettling vanilla sound cues (cave ambience, Enderman
  stare) played near one player at a time.
- **The Stalker** — a rare, brief sighting of a still, glowing zombie figure
  a short distance behind you that despawns itself after ~5 seconds.
- **Screen effect** — a slow-fading dark vignette during "tense moments,"
  purely a rendering overlay.

Everything is configurable/toggleable in `config/noescape-common.toml`
after the first run, including a master on/off switch and an
`eventChancePerMinute` frequency dial.

This mod does **not** create files, run system commands, or touch anything
outside the Minecraft process. Effects are cosmetic and reversible — nothing
here can corrupt your world.

## Building the jar (Option A: GitHub Actions — no local setup needed)

This project includes `.github/workflows/build.yml`, which compiles the mod
on GitHub's own servers and hands you back a ready-made `noescape.jar` —
no Java, Gradle, or Forge installed on your own machine required.

1. Create a new **empty** repository on GitHub (public or private).
2. Upload/push everything in this folder to that repo (including the
   hidden `.github` folder — make sure your upload method doesn't skip
   dotfiles/dotfolders; drag-and-drop on github.com's web UI preserves them
   as long as you drop the whole extracted folder, or use `git push`).
3. Go to the repo's **Actions** tab. A workflow run named "Build NoEscape"
   should start automatically (or click "Run workflow" if it doesn't).
4. Wait for it to finish (a couple minutes) — a green check means success.
5. Click into the finished run, scroll to **Artifacts**, and download
   `noescape-jar` — that's a zip containing your compiled `noescape.jar`.
6. Unzip it and drop `noescape.jar` into your `.minecraft/mods` folder.

If the run fails, click into the failed step's log — it'll usually point to
a typo or version mismatch, and I can help you fix it if you paste the
error back to me.

## Building the jar (Option B: locally)

This source is meant to be dropped into the official **Forge 1.20.1 MDK**
(Mod Development Kit), because the actual Minecraft/Forge binaries and the
Gradle wrapper jar are large, versioned, and licensed by Forge/Mojang — they
aren't something to bundle or fetch from inside a chat sandbox.

1. Download the Forge 1.20.1 MDK from https://files.minecraftforge.net
   (pick `1.20.1 - Recommended`, then "Mdk" download).
2. Unzip it, and copy everything from this project into it, overwriting:
   - `build.gradle`
   - `gradle.properties`
   - `settings.gradle`
   - `src/` (entirely)
3. In the project root, run:
   - Linux/macOS: `./gradlew build`
   - Windows: `gradlew.bat build`
4. The first run downloads Minecraft/Forge dependencies (needs internet).
5. Your finished mod will be at `build/libs/noescape.jar`.
6. Drop `noescape.jar` into your `.minecraft/mods` folder (Forge 1.20.1
   installed) and launch.

## Project layout
```
src/main/java/com/noescape/
  NoEscapeMod.java              - mod entry point
  config/NoEscapeConfig.java    - toggles & frequency dial
  event/ServerHorrorEvents.java - whispers, ambient sound, stalker spawn
  client/ClientHorrorEvents.java- drives the fading vignette intensity
  client/HorrorOverlay.java     - renders the vignette
src/main/resources/
  META-INF/mods.toml            - mod metadata Forge requires
  pack.mcmeta
```

## Extending it
Ideas if you want to take this further, in order of effort:
- Add more whisper strings / make them biome- or time-of-day-specific.
- Add custom ambient sound files under
  `src/main/resources/assets/noescape/sounds/` + a `sounds.json`.
- Give the Stalker a custom texture/model instead of reusing the zombie.
- Add a `/noescape` command to let players self-toggle intensity.
