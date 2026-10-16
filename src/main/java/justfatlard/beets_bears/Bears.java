package justfatlard.beets_bears;

import java.util.List;
import java.util.Set;

import justfatlard.pandorical.api.EntityRegistration;
import justfatlard.pandorical.api.ItemRegistration;
import justfatlard.pandorical.api.PandoricalApi;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

/**
 * The two new bears as the game knows them: their entity types, their spawn eggs, and how
 * Pandorical draws them.
 *
 * <p>Each is its own animal, {@link ForestBear}, built on the polar bear. Pandorical clients are
 * told to stand a polar bear in for it, drawn by the polar bear's renderer with the bear's own body
 * and coat; vanilla clients are sent a polar bear outright, the right size because the size is the
 * scale attribute every client reads. The type is registered at exactly the polar bear's size for
 * the same reason: the client builds the stand-in from the polar bear's dimensions, and a hitbox
 * the client did not agree with would be one the player keeps missing.
 */
public final class Bears {
	private Bears() {}

	/**
	 * @param id    the type's path, and the name its body, coat and egg are filed under
	 * @param scale against a polar bear, which is 1
	 * @param name  what a client without this mod's names reads instead
	 */
	public enum Kind {
		BLACK("black_bear", 0.8F, "Black Bear"),
		BROWN("brown_bear", 1.25F, "Brown Bear");

		private final String id;
		private final float scale;
		private final String name;

		Kind(String id, float scale, String name) {
			this.id = id;
			this.scale = scale;
			this.name = name;
		}

		public String id() { return id; }
		public float scale() { return scale; }
		public String displayName() { return name; }

		public EntityType<ForestBear> type() {
			return switch (this) {
				case BLACK -> BLACK_BEAR;
				case BROWN -> BROWN_BEAR;
			};
		}

		public Item egg() {
			return switch (this) {
				case BLACK -> BLACK_BEAR_SPAWN_EGG;
				case BROWN -> BROWN_BEAR_SPAWN_EGG;
			};
		}

		Identifier texture() {
			return Identifier.fromNamespaceAndPath(Main.MOD_ID, "textures/entity/" + id + ".png");
		}

		/** Its body: {@code assets/<mod>/pandorical/entity_models/<id>.json}, written by generate_textures.py. */
		Identifier model() {
			return Identifier.fromNamespaceAndPath(Main.MOD_ID, id);
		}
	}

	/** What the game scales the polar bear's model by as it builds it: MeshTransformer.scaling(1.2F). */
	private static final float POLAR_MODEL_SCALE = 1.2F;

	public static final EntityType<ForestBear> BLACK_BEAR = type(Kind.BLACK);
	public static final EntityType<ForestBear> BROWN_BEAR = type(Kind.BROWN);

	public static final Item BLACK_BEAR_SPAWN_EGG = egg(Kind.BLACK, BLACK_BEAR);
	public static final Item BROWN_BEAR_SPAWN_EGG = egg(Kind.BROWN, BROWN_BEAR);

	/**
	 * The size goes in through the type's default attributes rather than at spawn, so every way a
	 * bear can come into the world gets it: a natural spawn, an egg, a cub being born, and a
	 * {@code /summon} that hands it data of its own.
	 *
	 * <p>The spawn rule is the ordinary animal one, grass and daylight. It is what a polar bear's
	 * own rule falls back to anywhere outside the ice biomes, so a forest qualifies as it always
	 * did; the polar bear's rule itself is written for the polar bear's type and nothing else.
	 */
	private static EntityType<ForestBear> type(Kind kind) {
		return FabricEntityType.Builder.createMob(ForestBear::new, MobCategory.CREATURE, mob -> mob
				.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
					Animal::checkAnimalSpawnRules)
				.defaultAttributes(() -> ForestBear.createAttributes(kind)))
			// Everything else the polar bear's type says about itself, so the game treats the two
			// alike wherever it asks the type rather than the class. The entity type tags that
			// name the polar bear name these two as well, in data/minecraft/tags/entity_type.
			.immuneTo(BlockTags.POLAR_BEAR_IMMUNE_TO)
			.sized(EntityTypes.POLAR_BEAR.getWidth(), EntityTypes.POLAR_BEAR.getHeight())
			.eyeHeight(EntityTypes.POLAR_BEAR.getDimensions().eyeHeight())
			.clientTrackingRange(EntityTypes.POLAR_BEAR.clientTrackingRange())
			.build(ResourceKey.create(Registries.ENTITY_TYPE, id(kind.id())));
	}

	private static Item egg(Kind kind, EntityType<ForestBear> type) {
		return new SpawnEggItem(new Item.Properties()
			.setId(ResourceKey.create(Registries.ITEM, id(kind.id() + "_spawn_egg")))
			.spawnEgg(type));
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Main.MOD_ID, path);
	}

	public static void register() {
		for (Kind kind : Kind.values()) {
			Registry.register(BuiltInRegistries.ENTITY_TYPE, id(kind.id()), kind.type());
			Identifier eggId = id(kind.id() + "_spawn_egg");
			Registry.register(BuiltInRegistries.ITEM, eggId, kind.egg());

			PandoricalApi.content().registerEntity(kind.type(), new EntityRegistration()
				.base(EntityTypes.POLAR_BEAR)
				// A body of its own, animated by the polar bear's model class: the parts carry the
				// polar bear's names and pivots, so it walks and stands up as a polar bear does.
				.model(kind.model())
				.texture(kind.texture())
				// The polar bear's renderer draws its own model at 1.2 times its written size, and
				// does nothing of the sort to geometry it is handed, so the body asks for it here.
				// A cub is the same body at its age scale, wearing the same coat.
				.drawScale(POLAR_MODEL_SCALE)
				.vanillaFallback());
			PandoricalApi.content().registerItem(eggId.toString(), new ItemRegistration());
		}

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(output -> {
			output.accept(BLACK_BEAR_SPAWN_EGG);
			output.accept(BROWN_BEAR_SPAWN_EGG);
		});
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

	/** Which bear this is, or null for a polar bear and for anything that is not a bear at all. */
	public static @Nullable Kind of(Entity entity) {
		return entity instanceof ForestBear bear ? bear.kind() : null;
	}
}
