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
need Pandorical client-side to see the bears' own bodies and coats; without it the
bears are there, correctly sized and tempered, but drawn as polar bears.

## The art

```
python3 generate_textures.py
python3 generate_icon.py
```

`generate_textures.py` is the one description of each bear: its parts and the cubes on them,
written where each cube sits on the standing animal. From that it writes the model Pandorical
builds (`pandorical/entity_models/<bear>.json`), places every cube on a 128x64 sheet, and paints
the sheet face by face by asking the bear what colour each spot on its body is - top of the back,
side of a leg, front of the muzzle - so the model and its coat cannot disagree. Change the bear
there and re-run it; never edit the JSON or the PNGs by hand.

The parts are the polar bear's, by name and by pivot, because the polar bear's model class is what
animates them: it finds head, body and the four legs by name, and when the bear stands it turns the
body about its pivot and lifts the head and front legs by fixed amounts. Move a pivot and a standing
bear comes apart. Two rules the standing pose enforces on top of that: the body's cubes must
reach back to its pivot (z=12), or the turned-up body lifts off the hind legs; and the head is
lifted and tipped by fixed amounts tuned to the polar bear's head, so a head that is shallower
underneath needs something under it (the black bear's throat cube) or it hangs clear of the
shoulders. `BearsPictured` takes each standing bear close from the side to check exactly this. The cubes are written at the polar bear's unscaled size, and `Bears` registers a
draw scale of 1.2 to match the scale the game builds the polar bear's own model at.

The script also paints the two spawn eggs over the polar bear's egg out of the Loom cache, colour by
colour, with a muzzle patch round the nose.

`generate_icon.py` crops the brown bear's face out of the sheet the first script
wrote, finding the head and muzzle through the model it wrote, so the icon cannot
drift away from the mod's own art.

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
`BearsPictured` stands a polar, a black and a brown bear in a row with a cub of each, and takes them
from the side, three-quarters, the front, each one close, and up on their hind legs; the pictures
land in `build/run/clientGameTest/screenshots`, and with `-Ppictures` in `screenshots/` too.

## What to check by hand

- **The bodies.** A Pandorical client should see a black and a brown bear with their own shapes,
  walking and rearing up in one piece, and a cub that is the same bear drawn small; a vanilla client
  should see white polar bears at the two sizes. The client log should have no
  `NotUnderstood.ENTITY_MODEL` and no "cannot stand in" line for either bear.
- **The 1.0.x migration in a real world.** Open a world saved by 1.0.x and look at its bears.
  `Legacy` and `LegacyBearMixin` can be deleted once every such world has been opened once.
