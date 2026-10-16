package justfatlard.beets_bears;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.MobCategory;

/**
 * Putting bears in the woods: black bears in woodland, brown bears in conifer forest.
 *
 * <p>Added on top of what lives there, never in place of it, and the polar bear's own spawns are
 * left exactly as the game has them.
 */
public final class Spawns {
	private Spawns() {}

	/**
	 * Rarer than the animals you farm and rarer than a wolf, because meeting one should be an
	 * event. A pair at most: bears are not a herd.
	 */
	private static final int BLACK_WEIGHT = 4;
	private static final int BROWN_WEIGHT = 3;
	private static final int MIN_GROUP = 1;
	private static final int MAX_GROUP = 2;

	public static void register() {
		BiomeModifications.addSpawn(
			BiomeSelectors.includeByKey(Bears.blackBiomes()),
			MobCategory.CREATURE, Bears.BLACK_BEAR, BLACK_WEIGHT, MIN_GROUP, MAX_GROUP);

		BiomeModifications.addSpawn(
			BiomeSelectors.includeByKey(Bears.brownBiomes()),
			MobCategory.CREATURE, Bears.BROWN_BEAR, BROWN_WEIGHT, MIN_GROUP, MAX_GROUP);
	}
}
