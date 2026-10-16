package justfatlard.beets_bears.integration;

import justfatlard.beets_bears.Bears;
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
 * <p>That mod lists its animals by type, and knows the polar bear and nothing else, so the black and
 * brown bears are added outright, salmon first: salmon stays what a bear is best known for wanting,
 * and the tip over a wild one reads the same for all three. Honey and beetroot are a second way in
 * for each - the same two that quiet a bear, so there is one thing to learn rather than two. Its
 * armour is drawn by class, and both bears are polar bears by class, so that part needs nothing.
 *
 * <p>Must only be touched behind a mod-loaded check: it names better-companions types directly and
 * loading it without that mod throws.
 */
public final class CompanionBears {
	private CompanionBears() {}

	public static void register() {
		CompanionSpecies.alsoOffer(EntityTypes.POLAR_BEAR, Items.HONEY_BOTTLE, Items.BEETROOT);
		for (Bears.Kind kind : Bears.Kind.values()) {
			CompanionSpecies.alsoOffer(kind.type(),
				Items.SALMON, Items.HONEY_BOTTLE, Items.BEETROOT);
		}
	}
}
