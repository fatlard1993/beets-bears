# Beets & Bears

Black bears in the woods and brown bears in the conifers, built out of the polar bear the game
already has. Hold out honey or a beetroot and a bear will stop meaning you harm.

## What This Mod Does

The game has exactly one bear, and it lives on the ice. Every forest in the world is empty of the
one animal that ought to be the reason you look over your shoulder in it. This puts a bear in the
woods, a bigger one in the pines, and gives you something to do about it other than kill it.

Server-side; Pandorical carries the client's half.

## The Three Bears

| Bear | Where | Size | Temper |
|------|-------|------|--------|
| Black Bear | forest, birch, dark oak, flower forest, jungle, sparse jungle | four-fifths of a polar bear | keeps away from people, fights only if you start it |
| Brown Bear | taiga, snowy taiga, both old growth taigas, grove, windswept forest | a quarter larger | a polar bear's temper, cubs and all |
| Polar Bear | unchanged | unchanged | unchanged |

Size is not only for the look of it: a bear's health goes with its size, so the brown bear that
looks harder to kill is harder to kill, and the black bear that looks slighter is slighter.

**Where a bear is born decides what it is.** A polar bear that wanders south into a forest is a
polar bear a long way from home, not a black bear, and a black bear that wanders onto the ice stays
a black bear. Cubs are whatever their parent was, wherever they are born.

## None Of Them Is A New Animal

All three are `minecraft:polar_bear`. A server-side mod cannot hand a vanilla client a new creature
to draw, so a genuinely new bear would be an invisible one for anybody not running the mod
themselves. What a client *can* be told is that a familiar animal is a different size and wearing a
different coat, and that is what this does: the bears are resized through the game's own scale
attribute and recoloured through Pandorical's entity overlay.

Everything that follows from that is deliberate. They stand on their hind legs, swim, breed, panic
in fire and raise cubs exactly as polar bears, because they are polar bears. A command that looks
for polar bears finds them. Anything that changes what a polar bear drops changes what they drop.

**One egg, and the animal is just a Bear.** The spawn egg now hatches whichever bear the place
calls for, so "Polar Bear Spawn Egg" was wrong two times out of three; it reads **Bear Spawn Egg**,
and the animal itself is introduced as a **Bear** wherever the game says its name, death messages
included. The precise word comes back on the block-tip card, which is where a player is actually
asking which bear this is.

Which bear an animal is, is written on the animal as a scoreboard tag - `black_bear` or
`brown_bear`. Tags are saved with the entity by the game itself, survive this mod being removed, and
can be read or set with `/tag` when something needs fixing by hand.

## Honey, Or A Beetroot

**Hold either one out to a bear and it settles.** It lets go of whatever anger it was carrying,
stops guarding its cubs against you, and will not start a fight. The honey bottle leaves its glass
behind, the way drinking one does.

It is not taming. A quieted bear does not follow you, does not wait where you put it, and is not
yours. It simply stops being a problem.

**Hit it and the deal is off.** A bear that kept trusting you through being attacked would be a toy,
not a bear. A black bear goes back to keeping its distance; a brown bear goes back to being a brown
bear.

## Keeping One

With [better-companions](../better-companions) installed, the same two foods will also make a bear
yours - held out while **crouching**, which is that mod's gesture for keeping an animal, against
this mod's standing gesture for calming one. From there it is a companion like any other: it
follows, it waits, it wears armour, it comes when whistled for.

Salmon still works too, and still reads as the thing a bear is best known for wanting. Honey and
beetroot are a second way in rather than a replacement.

Without better-companions there is no taming here at all. This mod quiets bears; keeping them is
that mod's job, and having two systems that both half-owned the same animal would be worse than
having one.

## Looking At One

With [block-tip](../block-tip) installed, the card names the bear in front of you properly - **Black
Bear**, **Brown Bear** or **Polar Bear** - rather than the Polar Bear all three are underneath. It
also says what to do about it: a wild one reads *Honey or a beetroot to settle*, and one you have
already settled reads *Settled*.

The card is the only place the three are named apart, and deliberately so. The alternative is
giving each bear a custom name, which fixes the card and then hangs the word in the air over the
animal - and a wild bear with its name floating above it is somebody's pet.

## Meat

**With [lets-cook](../lets-cook) installed, this mod stays out of it.** That mod already butchers a
polar bear properly - a lean cut with fat on it, sized by Looting, cooked if the animal burned - and
since all three bears are polar bears, its work covers them without being asked.

**Without it, a bear drops beef**, because vanilla gives a polar bear nothing but the fish in its
stomach and a forest full of bears you have no reason to hunt is a strange thing to add. A black
bear gives one or two, a brown bear two or three. Cubs drop nothing.

Only one of the two mods is ever the one deciding, so there is no install where you get both lots.

## Spawning

Bears are rarer than the animals you farm and rarer than wolves, and come alone or in pairs. They
are added to the biomes above and nothing is taken away, so every animal that lived in those woods
before still does.

A polar bear's spawn rule only insists on ice and snow inside the biomes tagged for it. Everywhere
else it falls back to the ordinary animal rule - grass, and daylight - so no override was needed to
put one in a forest.

## Pandorical

Pandorical is required on the server; the mod will not load without it. The two coats are sent to
Pandorical clients as entity overlays.

**Without Pandorical client-side**, a player still meets the bears, and they are still the right
size, the right temper and carry the right drops - they are simply all white, because the client
was never handed the coat. Nothing breaks; the bears are just harder to tell apart.

## Development

Installing and the art pipeline are in [DEVELOPMENT.md](DEVELOPMENT.md).

## License

MIT, see [LICENSE](LICENSE).
