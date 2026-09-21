package justfatlard.beets_bears.integration;

import justfatlard.beets_bears.Bears;
import justfatlard.beets_bears.Temper;
import justfatlard.block_tip.api.BlockTipApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.polarbear.PolarBear;

/**
 * Telling the three bears apart when you are looking at one.
 *
 * <p>All three are polar bears underneath, so the game introduces every one of them as a Polar
 * Bear: the black bear in the oak wood says it, and so does the brown one in the pines. That is the
 * one place the trick this mod plays shows through, and it shows through exactly where a player is
 * asking what they are looking at.
 *
 * <p>block-tip's namer is built for this - its own note calls it the hook for "an entity that is
 * one thing wearing another's type". A custom name would also fix the card and would then hang the
 * word in the air over the animal, which turns a wild bear into somebody's pet.
 *
 * <p>Compiled against block-tip's API and guarded at the call site by a mod-loaded check, so a
 * server without it never loads this class.
 */
public final class BearTips {
	private BearTips() {}

	public static void register() {
		BlockTipApi.nameEntity(BearTips::name);
		BlockTipApi.describeEntity(BearTips::describe);
	}

	/**
	 * Polar Bear is spelled out rather than left to the game, because with all three renamed to
	 * Bear everywhere else, a card that fell back to the entity's own name would call the one on
	 * the ice a Bear while naming the other two exactly.
	 */
	private static String name(Entity entity, ServerPlayer player) {
		if (!(entity instanceof PolarBear bear)) return null;
		Bears.Kind kind = Bears.of(bear);
		if (kind == null) return "Polar Bear";
		return switch (kind) {
			case BLACK -> "Black Bear";
			case BROWN -> "Brown Bear";
		};
	}

	/**
	 * What to do about it, which for a bear is the only question worth answering.
	 *
	 * <p>Silent for a cub, and silent once a bear is settled and there is nothing left to do. A
	 * bear somebody already keeps is better-companions' line to write, not this one's.
	 */
	private static String describe(Entity entity, ServerPlayer player) {
		if (!(entity instanceof PolarBear bear) || bear.isBaby()) return null;
		if (Temper.isCalm(bear)) return "Settled";
		return "Honey or a beetroot to settle";
	}
}
