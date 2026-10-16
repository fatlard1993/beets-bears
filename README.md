# Beets Bears

Black bears in the woods and brown bears in the conifers, built out of the polar bear the game already has. Hold out honey or a beetroot and a bear will stop meaning you harm.

## What This Mod Does

The game has exactly one bear, and it lives on the ice. Every forest in the world is empty of the one animal that ought to be the reason you look over your shoulder in it. This puts a bear in the woods, a bigger one in the pines, and gives you something to do about it other than kill it.

Server-side; Pandorical carries the client's half.

## The Three Bears

| Bear | Where | Size | Temper |
|------|-------|------|--------|
| Black Bear | forest, birch, dark oak, flower forest, jungle, sparse jungle | four-fifths of a polar bear | keeps away from people, fights only if you start it |
| Brown Bear | taiga, snowy taiga, both old growth taigas, grove, windswept forest | a quarter larger | a polar bear's temper, cubs and all |
| Polar Bear | unchanged | unchanged | unchanged |

Size is not only for the look of it: a bear's health goes with its size, so the brown bear that looks harder to kill is harder to kill, and the black bear that looks slighter is slighter.

**A bear is what it was born.** A polar bear that wanders south into a forest is a polar bear a long way from home, not a black bear, and a black bear that wanders onto the ice stays a black bear. Cubs are whatever their parents were, wherever they are born, and a black bear and a brown bear will not breed with each other.

## Two New Animals, On The Polar Bear's Bones

The black bear and the brown bear are animals of their own, `beets-bears-justfatlard:black_bear` and `beets-bears-justfatlard:brown_bear`, and underneath each one is the polar bear's own code. They stand on their hind legs, swim, panic in fire and raise cubs exactly as polar bears do, because that is the code they run. What they add is their size, their shape and coat, and their temper.

A client with Pandorical draws each one with a body and coat of its own. The black bear is the slighter animal: still heavy-set, but flat across the back, with tall ears and a long, straight muzzle in tan, which is the thing to look for across a clearing. The brown bear carries a hump over its shoulders that stands higher than its head, a broad face with a short muzzle, small round ears at the corners of its head, a coat that is dark on the legs and belly and pale along the back, and long pale claws on its front feet. Both walk and rear up exactly as a polar bear does, because it is the polar bear's own animation moving them. A cub is its parent drawn small.

A client without it is shown a plain polar bear in its place, at the right size, because the size is the game's own scale attribute and every client reads that.

A command that looks for polar bears no longer finds them; ask for the bear by its own name. Anything that changes what a polar bear drops still changes what they drop, because each bear's loot is the polar bear's loot.

**An egg each.** The creative spawn egg tab has a **Black Bear Spawn Egg** and a **Brown Bear Spawn Egg**, and the Polar Bear Spawn Egg is the polar bear's again. Used on a grown bear, an egg hatches a cub of that bear, the way every spawn egg in the game does. The animals are introduced by their own names wherever the game says one, and a death message reads *Black Bear* or *Brown Bear* even on a client that has never heard of this mod.

**Bears from an older world come along.** Before this, a black or brown bear was a polar bear wearing a `black_bear` or `brown_bear` tag. The first time one of those loads, it comes back as the bear it was meant to be: same spot, same health, same age, same name, still settled if you had settled it, still yours if it was yours.

## Honey, Or A Beetroot

**Hold either one out to a bear and it settles.** It lets go of whatever anger it was carrying, stops guarding its cubs against you, and will not start a fight. The honey bottle leaves its glass behind, the way drinking one does.

It is not taming. A quieted bear does not follow you, does not wait where you put it, and is not yours. It simply stops being a problem.

**Hit it and the deal is off.** A bear that kept trusting you through being attacked would be a toy, not a bear. A black bear goes back to keeping its distance; a brown bear goes back to being a brown bear.

## Keeping One

With [better-companions](https://github.com/fatlard1993/better-companions) installed, the same two foods will also make a bear yours - held out while **crouching**, which is that mod's gesture for keeping an animal, against this mod's standing gesture for calming one. From there it is a companion like any other: it follows, it waits, it wears armour, it comes when whistled for.

Salmon still works too, and still reads as the thing a bear is best known for wanting. Honey and beetroot are a second way in rather than a replacement.

Without better-companions there is no taming here at all. This mod quiets bears; keeping them is that mod's job, and having two systems that both half-owned the same animal would be worse than having one.

## Looking At One

With [block-tip](https://github.com/fatlard1993/block-tip) installed, the card says what to do about the bear in front of you: a wild one reads *Honey or a beetroot to settle*, and one you have already settled reads *Settled*. It names the bear by itself, since each is its own animal with its own name, and credits this mod for the black and the brown.

## Meat

**With [lets-cook](https://github.com/fatlard1993/lets-cook) installed, this mod stays out of it.** That mod already butchers a polar bear properly: a lean cut with fat on it, sized by Looting, cooked if the animal burned. The black and brown bears drop whatever a polar bear drops, so its work covers them without being asked.

**Without it, a bear drops beef**, because vanilla gives a polar bear nothing but the fish in its stomach and a forest full of bears you have no reason to hunt is a strange thing to add. A black bear gives one or two, a brown bear two or three. Cubs drop nothing.

Only one of the two mods is ever the one deciding, so there is no install where you get both lots.

## Spawning

Bears are rarer than the animals you farm and rarer than wolves, and come alone or in pairs. They are added to the biomes above and nothing is taken away, so every animal that lived in those woods before still does.

They spawn by the ordinary animal rule, grass and daylight, which is the same rule a polar bear falls back on anywhere outside the ice. Polar bear spawns are left exactly as they were.

## Pandorical

Pandorical is required on the server; the mod will not load without it. It is what tells a client to draw each new bear with its own body and coat, and what sends a polar bear in its place to a client that cannot draw it.

**Without Pandorical client-side**, a player still meets the bears, and they are still the right size, the right temper, carry the right drops and go by the right names. They are simply all polar bears to look at, because the client was never handed the bodies or the coats. Nothing breaks; the bears are just harder to tell apart.

## Development

Installing and the art pipeline are in [DEVELOPMENT.md](DEVELOPMENT.md).

## License

MIT, see [LICENSE](LICENSE).
