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

`generate_textures.py` reads the vanilla polar bear sheet straight out of the
Loom cache and recolours it against luminance, so the shading, seams, eyes and
nose all land where the model expects them. Pixels darker than the coat are left
alone, which is what keeps the face a face.

`generate_icon.py` crops the brown bear's face out of the sheet the first script
wrote, so the icon cannot drift away from the mod's own art.

Both are idempotent and safe to re-run; neither needs the game running.

## What to check by hand

Nothing here is covered by a test, and three things are only true at runtime:

- **The mixins bind.** `defaultRequire` is 1, so a mismatched injector refuses
  to load the mod outright. Boot a dev server and look for `Done`.
- **A forest bear is black.** Spawn eggs go through the same spawn hook, so
  `/summon polar_bear` in a forest should produce a smaller black one.
- **A bear reloads dressed.** Coats live in memory on the client, so the test
  that matters is leaving the area and coming back.
