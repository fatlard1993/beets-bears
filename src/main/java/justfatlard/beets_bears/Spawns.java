package justfatlard.beets_bears;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;

/**
 * Putting bears in the woods.
 *
 * <p>Nothing here says "black bear" or "brown bear": it adds polar bear spawns to woodland and to
 * conifer forest, and {@link Bears#wildIn} decides what the animal that arrives turns out to be.
 * One rule, read in one place, rather than a spawn table and a naming table that can disagree.
 *
 * <p>The game needs no persuading to allow this. A polar bear's spawn rule only insists on ice and
 * snow inside the biomes tagged for it; everywhere else it falls back to the ordinary animal rule,
 * which is grass and daylight - so a forest already qualifies.
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
			MobCategory.CREATURE, EntityTypes.POLAR_BEAR, BLACK_WEIGHT, MIN_GROUP, MAX_GROUP);

		BiomeModifications.addSpawn(
			BiomeSelectors.includeByKey(Bears.brownBiomes()),
			MobCategory.CREATURE, EntityTypes.POLAR_BEAR, BROWN_WEIGHT, MIN_GROUP, MAX_GROUP);
	}
}
