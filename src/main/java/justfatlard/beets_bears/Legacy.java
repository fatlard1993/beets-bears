package justfatlard.beets_bears;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.storage.ValueInput;

/**
 * TEMPORARY. Bringing back the bears saved by 1.0.x, which were polar bears wearing a tag.
 *
 * <p>Up to 1.0.3 a black bear was a {@code minecraft:polar_bear} with the scoreboard tag
 * {@code black_bear} (or {@code brown_bear}) and a size modifier on its scale and health. A saved
 * one is read back as the new type instead, at the moment its data is read and before the animal
 * exists: the same save, so the same UUID, position, health, age, name, calm tag, owner and
 * anything another mod attached to it, with nothing despawned and respawned. Then {@link #shed}
 * takes off the tag and the old modifier, which the new type's own default size replaces.
 *
 * <p>Delete this class, {@code LegacyBearMixin} and the call in {@link ForestBear#load} once the
 * worlds that had 1.0.x bears have all been opened once.
 */
public final class Legacy {
	private Legacy() {}

	/** The type a save that says polar bear should really come back as. */
	public static Optional<EntityType<?>> retype(Optional<EntityType<?>> type, ValueInput input) {
		if (type.isEmpty() || type.get() != EntityTypes.POLAR_BEAR) return type;
		List<String> tags = input.read("Tags", Codec.STRING.listOf()).orElse(List.of());
		for (Bears.Kind kind : Bears.Kind.values()) {
			if (tags.contains(kind.id())) return Optional.of(kind.type());
		}
		return type;
	}

	/** Drop what 1.0.x put on the animal, so its size is not applied twice. */
	static void shed(ForestBear bear) {
		Bears.Kind kind = bear.kind();
		bear.removeTag(kind.id());
		Identifier oldSize = Identifier.fromNamespaceAndPath(Main.MOD_ID, "size_" + kind.id());
		for (Holder<Attribute> attribute : List.of(Attributes.SCALE, Attributes.MAX_HEALTH)) {
			AttributeInstance instance = bear.getAttribute(attribute);
			// The old bear's base was a polar bear's, with the modifier making up the difference;
			// the new one's is the size itself. Same total, so the health it was saved with fits.
			if (instance != null && instance.removeModifier(oldSize)) {
				instance.setBaseValue(DefaultAttributes.getSupplier(kind.type()).getBaseValue(attribute));
			}
		}
	}
}
