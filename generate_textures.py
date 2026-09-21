#!/usr/bin/env python3
"""
The two coats, recoloured from the polar bear's own texture.

Drawn from vanilla rather than painted fresh so every patch lands exactly where the polar bear
model expects it: the eyes, the nose, the pads and the seams are already in the right pixels, and
a hand-drawn sheet would have to rediscover all of them to look like anything but a smear.

The recolour is a tint against luminance, so the shading vanilla already has survives - the darker
pixels of a black bear's coat are the ones that were darker on the polar bear.
"""

import zipfile
from pathlib import Path

from PIL import Image

CLIENT_JAR = Path.home() / ".gradle/caches/fabric-loom/26.3/minecraft-client-only.jar"
SOURCE = "assets/minecraft/textures/entity/bear/polarbear.png"
OUT = Path(__file__).parent / "src/main/resources/assets/beets-bears-justfatlard/textures/entity"

# Darkest and lightest the coat gets. The polar bear's own range is nearly white throughout, so
# each coat is really a ramp picked here and the vanilla texture only says where along it to sit.
COATS = {
    "black_bear": ((10, 9, 10), (58, 52, 52)),
    "brown_bear": ((58, 38, 22), (156, 114, 74)),
}

# Left alone in both coats: a bear's muzzle is paler than the rest of it, and its eyes and nose are
# not coat at all. Without this the face flattens into one colour and the animal stops reading as
# having a face.
DARK_ENOUGH_TO_BE_FEATURE = 90


def recolour(source: Image.Image, dark, light) -> Image.Image:
    out = Image.new("RGBA", source.size)
    src = source.convert("RGBA").load()
    dst = out.load()
    for y in range(source.height):
        for x in range(source.width):
            r, g, b, a = src[x, y]
            if a == 0:
                dst[x, y] = (0, 0, 0, 0)
                continue
            luma = (r * 299 + g * 587 + b * 114) // 1000
            if luma < DARK_ENOUGH_TO_BE_FEATURE:
                dst[x, y] = (r, g, b, a)
                continue
            # Where this pixel sits between the polar bear's own darkest coat pixel and white.
            t = (luma - DARK_ENOUGH_TO_BE_FEATURE) / (255 - DARK_ENOUGH_TO_BE_FEATURE)
            dst[x, y] = (
                round(dark[0] + (light[0] - dark[0]) * t),
                round(dark[1] + (light[1] - dark[1]) * t),
                round(dark[2] + (light[2] - dark[2]) * t),
                a,
            )
    return out


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(CLIENT_JAR) as jar, jar.open(SOURCE) as handle:
        polar = Image.open(handle).convert("RGBA")
        polar.load()

    for name, (dark, light) in COATS.items():
        path = OUT / f"{name}.png"
        recolour(polar, dark, light).save(path)
        print(f"{path.relative_to(Path(__file__).parent)}  {polar.width}x{polar.height}")


if __name__ == "__main__":
    main()
