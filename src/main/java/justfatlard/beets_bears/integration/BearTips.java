package justfatlard.beets_bears.integration;

import justfatlard.beets_bears.Temper;
import justfatlard.block_tip.api.BlockTipApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.polarbear.PolarBear;

/**
 * What to do about the bear you are looking at.
 *
 * <p>Naming it is no longer this class's job: each bear is its own type with its own name, so the
 * card says Black Bear, Brown Bear or Polar Bear by itself, and credits this mod for the first two.
 *
 * <p>Compiled against block-tip's API and guarded at the call site by a mod-loaded check, so a
 * server without it never loads this class.
 */
public final class BearTips {
	private BearTips() {}

	public static void register() {
		BlockTipApi.describeEntity(BearTips::describe);
	}

	/**
	 * For a bear, the only question worth answering.
	 *
	 * <p>All three bears, since honey and beetroot settle all three. Silent for a cub, and silent once
	 * a bear is settled and there is nothing left to do. A bear somebody already keeps is
	 * better-companions' line to write, not this one's.
	 */
	private static String describe(Entity entity, ServerPlayer player) {
		if (!(entity instanceof PolarBear bear) || bear.isBaby()) return null;
		if (Temper.isCalm(bear)) return "Settled";
		return "Honey or a beetroot to settle";
	}
}
