#!/usr/bin/env python3
"""The mod icon: a brown bear's face, cut straight out of the coat the mod generates."""

from pathlib import Path

from PIL import Image

HERE = Path(__file__).parent
COAT = HERE / "src/main/resources/assets/beets-bears-justfatlard/textures/entity/brown_bear.png"
OUT = HERE / "src/main/resources/assets/beets-bears-justfatlard/icon.png"

# The face on the polar bear's sheet: the patch with both eyes and the nose on it.
FACE = (2, 4, 22, 20)
SIZE = 128
# Beetroot, for the half of the name that is not the bear.
BACKGROUND = (92, 26, 42, 255)


def main() -> None:
    face = Image.open(COAT).convert("RGBA").crop(FACE)
    scale = SIZE // max(face.width, face.height)
    face = face.resize((face.width * scale, face.height * scale), Image.NEAREST)

    icon = Image.new("RGBA", (SIZE, SIZE), BACKGROUND)
    icon.paste(face, ((SIZE - face.width) // 2, (SIZE - face.height) // 2), face)
    OUT.parent.mkdir(parents=True, exist_ok=True)
    icon.save(OUT)
    print(f"{OUT.relative_to(HERE)}  {SIZE}x{SIZE}")


if __name__ == "__main__":
    main()
