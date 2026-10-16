# Development

## Building

```
./gradlew build
```

or, from anywhere in the suite, `mc-build beets-bears`.

Compiles against Pandorical's working tree as a subproject, and against
better-companions' built jar as an optional compile-only dependency. If
better-companions has not been built, the build stops and says so rather than
quietly dropping the integration.

## Installing

Server-side. Drop the jar in the server's `mods/` alongside Pandorical. Players
need Pandorical client-side to see the bears' coats; without it the bears are
there, correctly sized and tempered, but all white.

## The art

```
python3 generate_textures.py
python3 generate_icon.py
```

`generate_textures.py` reads the vanilla polar bear and polar bear cub sheets, and the polar bear's
spawn egg, straight out of the Loom cache and recolours each against luminance, so the shading,
seams, eyes and nose all land where the model expects them. Pixels darker than the coat are left
alone, which is what keeps the face a face. The coats are registered as each bear's skin and cub
skin, so they must keep the polar bear's own layout, which drawing them from it guarantees.

`generate_icon.py` crops the brown bear's face out of the sheet the first script
wrote, so the icon cannot drift away from the mod's own art.

Both are idempotent and safe to re-run; neither needs the game running.

## Tests

```
xvfb-run -a -s "-screen 0 2048x1152x24" ./gradlew runClientGameTest
```

`SmokeTest` loads the mod into a world and checks a black bear is shy once and a brown bear not at
all. `BearsAreTheirOwnAnimals` spawns each bear and holds it to the readme: the polar bear's
hitbox, its own scale and health however it arrives (egg-style spawn, a cub growing up, `/summon`
with data), its name and the plain-word fallback, cubs of its own kind and no crossbreeding, its
own egg, an untouched polar bear, a polar bear sent to a vanilla client and the bear itself to a
Pandorical one, drawn by the polar bear's renderer. It also saves a bear exactly as 1.0.x did, a
tagged polar bear with the old size modifier, hurt, named and settled, and reads it back the way a
chunk does, to check it returns as the right bear with nothing lost and the size applied once.

## What to check by hand

- **The coats.** A Pandorical client should see a black and a brown bear, and a cub in a cub's coat
  that changes as it grows up; a vanilla client should see white polar bears at the two sizes.
- **The 1.0.x migration in a real world.** Open a world saved by 1.0.x and look at its bears.
  `Legacy` and `LegacyBearMixin` can be deleted once every such world has been opened once.
