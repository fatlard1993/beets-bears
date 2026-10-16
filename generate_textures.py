#!/usr/bin/env python3
"""
The two bears' bodies and coats, and their spawn eggs: one description of each bear, written out
as the model Pandorical builds and the sheet it is painted on, so the two can never disagree.

Each bear is a list of parts and the cubes on them, in the game's model units (sixteen to a block,
y down, the feet at 24), written where the cube sits on the standing animal rather than relative to
its part, so they read as the bear they draw. The script works out each cube's place on the sheet,
writes the model JSON with those places in it, and then paints every face of every cube by asking
the bear what colour that spot on its body is: the top of the back, the side of a leg, the front of
the muzzle. Moving a cube moves its paint with it.

The parts are the polar bear's, by name and by pivot, because the polar bear's own model class is
what animates them: it finds head, body and four legs by name, swings the legs from those pivots,
and on standing turns the body up about its own pivot and lifts the head and front legs by fixed
amounts. Keeping the pivots where the polar bear has them is what keeps a standing bear in one
piece. The body is written unturned, where the polar bear's is turned a quarter about x; standing
adds the same turn to either, so the pose comes out the same.

The polar bear draws its model at 1.2 times its written size, and Pandorical does not do that to
geometry a mod ships; the registration asks for it as a draw scale instead, so these are written at
the polar bear's own unscaled size and come out beside one at the same size.

Usage: python3 generate_textures.py
"""

import json
import zipfile
from dataclasses import dataclass, field
from pathlib import Path

from PIL import Image

HERE = Path(__file__).parent
MOD_ID = "beets-bears-justfatlard"
ASSETS = HERE / "src/main/resources/assets" / MOD_ID
TEXTURES = ASSETS / "textures/entity"
MODELS = ASSETS / "pandorical/entity_models"
EGGS = ASSETS / "textures/item"
CLIENT_JAR = Path.home() / ".gradle/caches/fabric-loom/26.3/minecraft-client-only.jar"
EGG_SOURCE = "assets/minecraft/textures/item/polar_bear_spawn_egg.png"

SHEET = (128, 64)


@dataclass
class Cube:
    role: str
    origin: tuple   # where its least corner is on the standing bear
    size: tuple
    # The cube on the other side this one copies: same place on the sheet, drawn mirrored.
    twin: "Cube | None" = None
    uv: tuple = None

    @property
    def lo(self):
        return self.origin

    @property
    def hi(self):
        return tuple(o + s for o, s in zip(self.origin, self.size))


@dataclass
class Part:
    name: str
    pivot: tuple
    cubes: list = field(default_factory=list)


def mirrored(cube: Cube) -> Cube:
    """The same cube on the bear's other side, sharing its paint."""
    (x, y, z), (w, h, d) = cube.origin, cube.size
    return Cube(cube.role, (-x - w, y, z), cube.size, twin=cube)


def legs(front_pivot, front_cubes, hind_pivot, hind_cubes):
    """Four legs from the right-hand pair: the left pair mirrors them. Right is -x, as the game has it."""
    out = []
    for name, pivot, cubes in (("front_leg", front_pivot, front_cubes), ("hind_leg", hind_pivot, hind_cubes)):
        out.append(Part("right_" + name, pivot, cubes))
        out.append(Part("left_" + name, (-pivot[0], pivot[1], pivot[2]), [mirrored(c) for c in cubes]))
    return out


# --- The black bear --------------------------------------------------------------------------
#
# Smaller than the polar bear already (its scale attribute), and lighter-built than the brown bear,
# but still a heavy animal: a deep body on short, thick legs set close together under it. No hump,
# a back that is flat and rises a little to the rump, and a straight
# face - the forehead runs into a long, narrow muzzle without the step a brown bear has. The muzzle
# is tan, which is how one is told from across a clearing. Tall ears, set close and high.

BLACK_PARTS = [
    Part("head", (0, 10, -16), [
        Cube("head", (-3.5, 7, -18), (7, 6, 7)),
        Cube("muzzle", (-2, 9, -23), (4, 4, 5)),
        Cube("ear", (-3, 4, -15), (2, 4, 1)),
        # The throat and scruff, under the back of the head. Walking it is nearly all inside the head
        # and chest, showing only as a throat below the jaw. Standing, the polar bear's animation
        # lifts the head by fixed amounts and tips it forward, and this head is a texel shorter
        # underneath than the polar bear's: without it, the head hangs clear of the shoulders. Its
        # back edge is where the chest's top front corner lands in that pose. No deeper than this,
        # or it pushes out through the sides of the chest when the head turns.
        Cube("neck", (-3, 11, -15), (6, 4, 4)),
    ]),
    Part("body", (0, 9, 12), [
        # Back to the body's pivot: standing turns the body up about it, and a rump that stopped
        # short of it lifts clear of the hind legs.
        Cube("rump", (-6, 5, -1), (12, 11, 13)),
        Cube("chest", (-5.5, 6, -12), (11, 10, 11)),
    ]),
    # Close under the body rather than at its ends, which made a table of it. Still a gap between
    # front and hind, so a leg swinging back at a run does not pass through the one behind it. The
    # hind legs sit half a texel outside the rump: flush, their sides would share its plane.
    *legs((-3.5, 14, -5), [Cube("front_leg", (-6, 14, -8), (5, 10, 5))],
          (-4, 14, 6), [Cube("hind_leg", (-6.5, 14, 3), (5, 10, 6))]),
]
_ear = BLACK_PARTS[0].cubes[2]
BLACK_PARTS[0].cubes.append(mirrored(_ear))

# --- The brown bear --------------------------------------------------------------------------
#
# Bigger than the polar bear (its scale attribute). The hump over the shoulders is the thing: the
# highest point of the animal, above a head that is carried low, with the back falling away from it
# to the rump in one step. A broad head with a short muzzle set below the brow, which is the dished
# face. Small ears at the head's top corners, standing above it. Front legs thick, and long pale
# claws on them.

BROWN_PARTS = [
    Part("head", (0, 10, -16), [
        Cube("head", (-4, 6, -19), (8, 7, 7)),
        Cube("muzzle", (-2.5, 10, -22), (5, 3, 3)),
        # Forward, at the corners and two texels clear of the top, so from the front they stand
        # out against whatever is behind the head instead of reading as marks on the face.
        Cube("ear", (-4.5, 4, -18), (3, 3, 1)),
    ]),
    Part("body", (0, 9, 12), [
        # The back: higher than the chest, so it meets the hump two texels below its top and the
        # line from shoulder to tail is one fall rather than a staircase. It starts a texel inside
        # the hump, so the two never share a face.
        Cube("rump", (-6, 4, -5), (12, 12, 17)),
        Cube("chest", (-7, 5, -13), (14, 12, 12)),
        # As wide as the chest and standing on it, so the shoulders rise straight out of the
        # chest's sides. On it rather than sunk into it: two faces sharing a plane and an area
        # flicker, and these share only an edge.
        Cube("hump", (-7, 2, -12), (14, 3, 8)),
    ]),
    *legs((-4, 14, -8), [Cube("front_leg", (-6.5, 14, -11), (5, 10, 6)),
                         Cube("claws", (-6.5, 23, -13), (5, 1, 2))],
          (-4.5, 14, 7), [Cube("hind_leg", (-7, 14, 4), (5, 10, 7))]),
]
_ear = BROWN_PARTS[0].cubes[2]
BROWN_PARTS[0].cubes.append(mirrored(_ear))


# --- Painting --------------------------------------------------------------------------------
#
# A painter is asked for the colour of one texel: which cube, which face of it, and where on the
# standing bear that texel's middle is. Tones are flat, a few steps each, the way vanilla's animals
# are painted; the game's own face shading does the rest.

def rgb(hex_):
    return tuple(int(hex_[i:i + 2], 16) for i in (1, 3, 5)) + (255,)


def ragged(x, z, period=(0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1)):
    """A fixed step along a boundary between two tones, so it reads as fur meeting fur and not as a
    ruled line. A pattern, not noise: the same every run, and the same on both sides of the bear.
    Every step is at least two texels wide: a lone texel out of line reads as a spot, not as fur."""
    return period[int(abs(x) + abs(z)) % len(period)]


BLACK = {
    "hi": rgb("#3a3c48"),      # the cool shine along the back
    "coat": rgb("#25252b"),
    "low": rgb("#17161a"),     # belly and the undersides
    "pad": rgb("#0e0d10"),
    "claw": rgb("#4a4440"),
    "tan_hi": rgb("#b48c5c"),
    "tan": rgb("#9a734a"),
    "tan_lo": rgb("#7a5a3a"),
    "nose": rgb("#121012"),
    "eye": rgb("#0a0808"),
    "iris": rgb("#8a5a32"),
    "blaze": rgb("#c8b48c"),
    "ear_in": rgb("#3a3036"),
}


def paint_black(cube, face, x, y, z):
    p = BLACK
    role = cube.role
    if role == "muzzle":
        if face == "north":
            if y < 10 and abs(x) < 1:
                return p["nose"]
            return p["tan_lo"] if y > 12 else p["tan"]
        if face == "up":
            return p["nose"] if z < -22 and abs(x) < 1 else p["tan_hi"]
        if face == "down":
            return p["tan_lo"]
        return p["tan"] if y < 12 else p["tan_lo"]
    if role == "head":
        if face == "north":
            if 8 < y < 9 and abs(abs(x) - 2) < 0.1:
                return p["eye"]
            if 8 < y < 9 and abs(abs(x) - 3) < 0.1:
                return p["iris"]
            return p["coat"]
        if face == "up":
            return p["hi"]
        if face == "down":
            return p["low"]
        return p["hi"] if y < 8 else p["coat"]
    if role == "neck":
        if face == "north":
            # A small pale blaze on the throat, below the jaw.
            if (13 < y < 14 and 1 < abs(x) < 2) or (14 < y < 15 and abs(x) < 1):
                return p["blaze"]
            return p["coat"]
        if face == "down":
            return p["blaze"] if z < -14 and abs(x) < 1 else p["low"]
        return p["coat"] if y < 13 else p["low"]
    if role == "ear":
        if face == "north" and y > 4.6:
            return p["ear_in"]
        return p["hi"] if face == "up" or y < 5 else p["coat"]
    if role in ("rump", "chest"):
        if face == "up":
            return p["hi"]
        if face == "down":
            if role == "chest" and z < -11 and abs(x) < 1:
                return p["blaze"]
            return p["low"]
        top = cube.lo[1]
        if y < top + 1.5 + ragged(x, z):
            return p["hi"]
        if y > 13 + ragged(x, z):
            return p["low"]
        return p["coat"]
    if role in ("front_leg", "hind_leg"):
        if face == "down":
            return p["pad"]
        if y > 23:
            if face == "north":
                return p["claw"] if int(x - cube.lo[0]) % 2 == 0 else p["pad"]
            return p["pad"]
        if face == "up":
            return p["coat"]
        return p["coat"] if y < 20 else p["low"]
    raise ValueError(role)


BROWN = {
    "tip": rgb("#c4a070"),     # the pale, grizzled tips along the hump and the top of the back
    "light": rgb("#a27850"),
    "mid": rgb("#7a5634"),
    "dark": rgb("#553a24"),
    "foot": rgb("#3c281a"),
    "muzzle_hi": rgb("#b08a60"),
    "muzzle": rgb("#9a7550"),
    "muzzle_lo": rgb("#6e4e32"),
    "nose": rgb("#1a1210"),
    "eye": rgb("#120c0a"),
    "brow": rgb("#7a5634"),
    "claw": rgb("#e2d6ba"),
    "claw_gap": rgb("#3c281a"),
}


def paint_brown(cube, face, x, y, z):
    p = BROWN
    role = cube.role
    if role == "claws":
        if face == "south":
            return p["foot"]
        if face in ("west", "east"):
            return p["claw"]
        return p["claw"] if int(x - cube.lo[0]) % 2 == 0 else p["claw_gap"]
    if role == "muzzle":
        if face == "north":
            if y < 11 and abs(x) < 1.5:
                return p["nose"]
            if 11 < y < 12 and abs(x) < 0.5:
                return p["nose"]
            return p["muzzle_lo"] if y > 12 else p["muzzle"]
        if face == "up":
            return p["nose"] if z < -21 and abs(x) < 1.5 else p["muzzle_hi"]
        if face == "down":
            return p["muzzle_lo"]
        return p["muzzle"] if y < 12 else p["muzzle_lo"]
    if role == "head":
        if face == "north":
            if 8 < y < 9 and abs(abs(x) - 2.5) < 0.1:
                return p["eye"]
            if 7 < y < 8 and abs(abs(x) - 2.5) < 0.6:
                return p["brow"]
            if y > 11:
                return p["mid"]
            return p["light"]
        if face == "up":
            return p["tip"]
        if face == "down":
            return p["dark"]
        if y < 8 + ragged(x, z):
            return p["tip"]
        return p["light"] if y < 11 else p["mid"]
    if role == "ear":
        # Mid-brown, with a paler inside, so an ear is an ear and not a dark mark.
        middle = (cube.lo[0] + cube.hi[0]) / 2
        if face == "north" and y > 5 and abs(x - middle) < 0.6:
            return p["muzzle_hi"]
        return p["mid"]
    if role == "hump":
        # Its front stands right behind the head: darker, so the head keeps its outline against it
        # from the front instead of the two reading as one tall face.
        if face == "north":
            return p["light"] if y < 3 else p["mid"]
        # The fall from the shoulders to the back, in shade, so it reads as a slope turning away.
        if face == "south":
            return p["mid"]
        return p["tip"] if face == "up" or y < 3 + ragged(x, z) else p["light"]
    if role in ("rump", "chest"):
        if face == "up":
            return p["tip"] if role == "rump" else p["light"]
        if face == "down":
            return p["dark"]
        # The pale tips run on along the back from the hump.
        if role == "rump" and y < 5 + ragged(x, z):
            return p["tip"]
        if y < 9 + ragged(x, z):
            return p["light"]
        if y < 13 + ragged(x, z):
            return p["mid"]
        return p["dark"]
    if role in ("front_leg", "hind_leg"):
        return p["foot"] if face == "down" or y > 22 else p["dark"]
    raise ValueError(role)


BEARS = {
    "black_bear": (BLACK_PARTS, paint_black),
    "brown_bear": (BROWN_PARTS, paint_brown),
}


# --- The sheet -------------------------------------------------------------------------------

def footprint(cube):
    """A cube's rectangle on the sheet, in the game's box layout: two rows, the top and bottom
    above the four sides."""
    w, h, d = cube.size
    return 2 * (d + w), d + h


def place(parts):
    """Each cube that has paint of its own, given a free spot on the sheet. The head goes first, in
    the corner, where the icon looks for its face; the rest in the first gap each fits, biggest first."""
    width, height = SHEET
    taken = [[False] * width for _ in range(height)]
    own = [c for part in parts for c in part.cubes if c.twin is None]
    head = [c for c in own if c.role == "head"]
    rest = sorted((c for c in own if c.role != "head"), key=lambda c: (-footprint(c)[1], -footprint(c)[0]))
    for cube in head + rest:
        fw, fh = (int(v) for v in footprint(cube))
        spot = None
        for v in range(height - fh + 1):
            for u in range(width - fw + 1):
                if all(not taken[v + j][u + i] for j in range(fh) for i in range(fw)):
                    spot = (u, v)
                    break
            if spot:
                break
        if spot is None:
            raise SystemExit(f"no room on a {width}x{height} sheet for {cube.role} {cube.size}")
        for j in range(fh):
            for i in range(fw):
                taken[spot[1] + j][spot[0] + i] = True
        cube.uv = spot
    for part in parts:
        for c in part.cubes:
            if c.twin is not None:
                c.uv = c.twin.uv


def faces(cube):
    """Every texel of a cube's faces: where on the sheet, which face, and where its middle is on the
    standing bear. The layout is the game's own: in the top row the top then the bottom, in the row
    under it the bear's right side, its front, its left side and its back."""
    u, v = cube.uv
    w, h, d = (int(s) for s in cube.size)
    (x0, y0, z0), (x1, y1, z1) = cube.lo, cube.hi
    for i in range(w):
        for j in range(d):
            yield (u + d + i, v + j), "up", (x0 + i + .5, y0, z1 - j - .5)
            yield (u + d + w + i, v + j), "down", (x0 + i + .5, y1, z1 - j - .5)
    for k in range(h):
        y = y0 + k + .5
        for i in range(d):
            yield (u + i, v + d + k), "west", (x0, y, z1 - i - .5)
            yield (u + d + w + i, v + d + k), "east", (x1, y, z0 + i + .5)
        for i in range(w):
            yield (u + d + i, v + d + k), "north", (x0 + i + .5, y, z0)
            yield (u + 2 * d + w + i, v + d + k), "south", (x1 - i - .5, y, z1)


def paint(parts, painter):
    sheet = Image.new("RGBA", SHEET, (0, 0, 0, 0))
    pixels = sheet.load()
    for part in parts:
        for cube in part.cubes:
            if cube.twin is not None:
                continue
            for (px, py), face, (x, y, z) in faces(cube):
                pixels[px, py] = painter(cube, face, x, y, z)
    return sheet


def model(name, parts):
    """The model as Pandorical reads it: each cube relative to its part's pivot."""
    out = {}
    for part in parts:
        px, py, pz = part.pivot
        cubes = []
        for c in part.cubes:
            x, y, z = c.origin
            cube = {"origin": [x - px, y - py, z - pz], "size": list(c.size), "uv": list(c.uv)}
            if c.twin is not None:
                cube["mirror"] = True
            cubes.append(cube)
        out[part.name] = {"pivot": list(part.pivot), "cubes": cubes}
    return {
        "texture": f"{MOD_ID}:textures/entity/{name}.png",
        "texture_size": list(SHEET),
        "parts": out,
    }


def number(value):
    return int(value) if float(value).is_integer() else value


def tidy(value):
    if isinstance(value, dict):
        return {k: tidy(v) for k, v in value.items()}
    if isinstance(value, list):
        return [tidy(v) for v in value]
    if isinstance(value, float):
        return number(value)
    return value


def compact(data):
    """JSON a person can read: one line per cube."""
    text = json.dumps(tidy(data), indent="\t")
    import re
    # Short numeric arrays onto one line, then each cube object onto one line.
    text = re.sub(r"\[\s+([-\d.,\s]+?)\s+\]", lambda m: "[" + ", ".join(m.group(1).split()).replace(",,", ",") + "]", text)
    text = re.sub(r"\{\s+(\"origin\"[^{}]*?)\s+\}", lambda m: "{" + " ".join(m.group(1).split()) + "}", text)
    return text + "\n"


# --- The eggs --------------------------------------------------------------------------------
#
# Painted over the polar bear's egg, colour by colour: the shell's four tones, its outline, and the
# nose stay where the game drew them and take each bear's colours. Each bear also gets the patch
# round the nose its face has, which on the black bear is the tan muzzle it is known by.

EGG_ROLES = {
    (255, 255, 255): "shine", (244, 244, 238): "light", (213, 208, 203): "mid",
    (168, 163, 155): "shade", (130, 125, 124): "line", (115, 111, 109): "line_dark",
    (77, 75, 81): "nose", (47, 47, 59): "nose", (16, 16, 20): "nose",
}

EGG_COATS = {
    "black_bear": {
        "shine": BLACK["hi"], "light": rgb("#30313c"), "mid": BLACK["coat"], "shade": BLACK["low"],
        "line": rgb("#0c0b0e"), "line_dark": rgb("#060508"), "nose": BLACK["nose"],
        "muzzle": BLACK["tan"], "muzzle_lo": BLACK["tan_lo"],
    },
    "brown_bear": {
        "shine": BROWN["tip"], "light": BROWN["light"], "mid": BROWN["mid"], "shade": BROWN["dark"],
        "line": rgb("#2e1e12"), "line_dark": rgb("#22160c"), "nose": BROWN["nose"],
        "muzzle": BROWN["muzzle_hi"], "muzzle_lo": BROWN["muzzle"],
    },
}


def egg(source, coat):
    out = Image.new("RGBA", source.size, (0, 0, 0, 0))
    src, dst = source.load(), out.load()
    nose = [(x, y) for y in range(source.height) for x in range(source.width)
            if src[x, y][3] and EGG_ROLES.get(src[x, y][:3]) == "nose"]
    left, right = min(x for x, _ in nose), max(x for x, _ in nose)
    top, bottom = min(y for _, y in nose), max(y for _, y in nose)
    for y in range(source.height):
        for x in range(source.width):
            r, g, b, a = src[x, y]
            if a == 0:
                continue
            role = EGG_ROLES[(r, g, b)]
            # The muzzle: the shell round the nose, a pixel out each side and one below.
            if role in ("shine", "light", "mid", "shade") and left - 1 <= x <= right + 1 and top <= y <= bottom + 1:
                role = "muzzle_lo" if y == bottom + 1 else "muzzle"
            dst[x, y] = coat[role]
    return out


def main() -> None:
    TEXTURES.mkdir(parents=True, exist_ok=True)
    MODELS.mkdir(parents=True, exist_ok=True)
    for name, (parts, painter) in BEARS.items():
        place(parts)
        paint(parts, painter).save(TEXTURES / f"{name}.png")
        (MODELS / f"{name}.json").write_text(compact(model(name, parts)))
        print(f"{name}: {sum(len(p.cubes) for p in parts)} cubes, sheet and model written")

    with zipfile.ZipFile(CLIENT_JAR) as jar, jar.open(EGG_SOURCE) as handle:
        source = Image.open(handle).convert("RGBA")
        source.load()
    EGGS.mkdir(parents=True, exist_ok=True)
    for name, coat in EGG_COATS.items():
        egg(source, coat).save(EGGS / f"{name}_spawn_egg.png")
        print(f"{name}_spawn_egg written")


if __name__ == "__main__":
    main()
