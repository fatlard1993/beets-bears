package justfatlard.beets_bears;

import java.util.List;
import java.util.Set;

import justfatlard.pandorical.api.PandoricalApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jspecify.annotations.Nullable;

/**
 * The three bears, and which one a given animal is.
 *
 * <p>All three are {@link PolarBear}. The game will not let a server-side mod hand a client a new
 * entity to draw, so a new bear would be an invisible one on every vanilla client; what it will do
 * is let an existing entity be a different size and wear a different skin. So a black bear is a
 * polar bear at four-fifths the size with a black coat painted over it, and it hunts, sleeps,
 * breeds and stands up on its hind legs exactly as the bear the game already knows how to draw.
 *
 * <p>Which bear an animal is, is written on it as a tag rather than held in a table here. Tags ride
 * along in the entity's own save data with no work from us, survive this mod being removed, and can
 * be read and set from a command when something has gone wrong, which a private map cannot.
 */
public final class Bears {
	private Bears() {}

	/**
	 * @param tag     what the animal wears to say which bear it is
	 * @param scale   against a polar bear, which is 1
	 * @param texture the coat, drawn over the polar bear's own
	 */
	public enum Kind {
		BLACK("black_bear", 0.8F, "textures/entity/black_bear.png"),
		BROWN("brown_bear", 1.25F, "textures/entity/brown_bear.png");

		private final String tag;
		private final float scale;
		private final Identifier texture;

		Kind(String tag, float scale, String texture) {
			this.tag = tag;
			this.scale = scale;
			this.texture = Identifier.fromNamespaceAndPath(Main.MOD_ID, texture);
		}

		public String tag() { return tag; }
		public float scale() { return scale; }
		public Identifier texture() { return texture; }
	}

	/**
	 * Woodland for the black bear and conifer for the brown, which is roughly where the two live.
	 *
	 * <p>Listed by biome rather than by tag because the tags that look right do not cut where this
	 * needs cutting: {@code #is_forest} holds the taigas as well, and would put both bears
	 * everywhere and tell the player nothing about where they are.
	 */
	private static final Set<ResourceKey<Biome>> BLACK_BIOMES = Set.of(
		Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.FLOWER_FOREST,
		Biomes.JUNGLE, Biomes.SPARSE_JUNGLE);

	private static final Set<ResourceKey<Biome>> BROWN_BIOMES = Set.of(
		Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA,
		Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.GROVE, Biomes.WINDSWEPT_FOREST);

	public static List<ResourceKey<Biome>> blackBiomes() { return List.copyOf(BLACK_BIOMES); }
	public static List<ResourceKey<Biome>> brownBiomes() { return List.copyOf(BROWN_BIOMES); }

	/** One id per kind, so re-applying replaces rather than stacks. */
	private static Identifier scaleModifierId(Kind kind) {
		return Identifier.fromNamespaceAndPath(Main.MOD_ID, "size_" + kind.tag());
	}

	/** Which bear this is, or null for a polar bear and for anything that is not a bear at all. */
	public static @Nullable Kind of(Entity entity) {
		if (!(entity instanceof PolarBear)) return null;
		for (Kind kind : Kind.values()) {
			if (entity.entityTags().contains(kind.tag())) return kind;
		}
		return null;
	}

	/** The bear this place makes, or null where the place makes polar bears or none at all. */
	public static @Nullable Kind wildIn(LevelReader level, BlockPos pos) {
		Holder<Biome> biome = level.getBiome(pos);
		for (ResourceKey<Biome> key : BLACK_BIOMES) {
			if (biome.is(key)) return Kind.BLACK;
		}
		for (ResourceKey<Biome> key : BROWN_BIOMES) {
			if (biome.is(key)) return Kind.BROWN;
		}
		return null;
	}

	/**
	 * Make this bear one of ours, once, at the moment it spawns.
	 *
	 * <p>Only ever called from a spawn, never from a chunk loading, because a polar bear that walks
	 * south into a forest is a polar bear a long way from home and not a black bear. Where an
	 * animal is born decides what it is; where it has got to since does not.
	 */
	public static void becomeWild(PolarBear bear, Kind kind) {
		if (of(bear) != null) return;
		bear.addTag(kind.tag());
		applySize(bear, kind);
		bear.setHealth(bear.getMaxHealth());
	}

	/**
	 * The size, written into the animal's own attributes so it is saved with the animal.
	 *
	 * <p>Health rides on the size for the same reason a bigger animal is harder to kill, and
	 * because a brown bear that was only visually larger would be a lie the first time one fought
	 * back.
	 */
	private static void applySize(PolarBear bear, Kind kind) {
		permanent(bear.getAttribute(Attributes.SCALE), kind, kind.scale() - 1F);
		permanent(bear.getAttribute(Attributes.MAX_HEALTH), kind, kind.scale() - 1F);
	}

	private static void permanent(@Nullable AttributeInstance attribute, Kind kind, double amount) {
		if (attribute == null) return;
		attribute.removeModifier(scaleModifierId(kind));
		attribute.addPermanentModifier(new AttributeModifier(
			scaleModifierId(kind), amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
	}

	/**
	 * Put the coat back on, every time the animal loads.
	 *
	 * <p>The size is saved with the animal and the coat is not: Pandorical holds overlays in memory
	 * and drops them when an entity unloads, so this is the half that has to be said again. Cheap
	 * enough to say for every bear that loads rather than tracking which ones have been told.
	 */
	public static void dress(Entity entity) {
		Kind kind = of(entity);
		if (kind == null) return;
		PandoricalApi.entityOverlays().set(entity, kind.texture());
	}
}
