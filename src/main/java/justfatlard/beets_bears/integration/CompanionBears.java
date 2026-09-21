package justfatlard.beets_bears.integration;

import justfatlard.better_companions.CompanionSpecies;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;

/**
 * Bears as companions, when better-companions is here to keep them.
 *
 * <p>This mod quiets a bear; it never keeps one. Following you, waiting where you left it, wearing
 * armour and coming back when whistled for are all that mod's work, and duplicating any of it here
 * would mean two systems that both half-own the same animal.
 *
 * <p>All this adds is the two foods. Salmon stays what a bear is best known for wanting, so the tip
 * over a wild one still reads the way that mod wrote it, and honey and beetroot are a second way in
 * - the same two that quiet a bear, so there is one thing to learn rather than two.
 *
 * <p>Must only be touched behind a mod-loaded check: it names better-companions types directly and
 * loading it without that mod throws.
 */
public final class CompanionBears {
	private CompanionBears() {}

	public static void register() {
		CompanionSpecies.alsoOffer(EntityTypes.POLAR_BEAR, Items.HONEY_BOTTLE, Items.BEETROOT);
	}
}
