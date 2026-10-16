package justfatlard.beets_bears;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.player.Player;

/**
 * Which bears will start a fight with you, and which will not.
 *
 * <p>A polar bear charges anyone who walks near its cub, which is right for an animal you meet once
 * on an ice sheet and wrong for one living in the forest you built your house in. A brown bear keeps
 * that temper. A black bear runs from people instead, the way a real one does. Both still fight back
 * when hit, and a bear you have quieted with honey stops starting anything at all.
 *
 * <p>All three are settled the same way: not by pulling the polar bear's goals apart, but by saying
 * which players a bear is allowed to pick as a target. A goal removed by name breaks silently the
 * day the name changes, and the thing actually wanted here is one sentence - <em>it will not come
 * for you unless you hit it first</em> - which is exactly what {@link #mayHunt} says.
 */
public final class Temper {
	private Temper() {}

	/** What a black bear keeps between itself and you, in blocks. */
	private static final float SHY_DISTANCE = 12F;

	/** The tag a bear wears once it has been quieted; see {@link Calming}. */
	public static final String CALM_TAG = "calm";

	public static boolean isCalm(PolarBear bear) {
		return bear.entityTags().contains(CALM_TAG);
	}

	/**
	 * Whether this bear is allowed to come after this target.
	 *
	 * <p>Only players are ever refused, and only when the bear has no quarrel with them: a bear
	 * that has been hit knows who by, and nothing here stops it answering. Hunting foxes, and
	 * anger a player has genuinely earned, both go on untouched.
	 */
	public static boolean mayHunt(PolarBear bear, LivingEntity target) {
		if (!(target instanceof Player)) return true;
		if (bear.getLastHurtByMob() == target) return true;
		return !(isCalm(bear) || Bears.of(bear) == Bears.Kind.BLACK);
	}

	/**
	 * Give a black bear its nerves, once, as it is built.
	 *
	 * <p>Only an added goal, never a removed one: adding is safe against a version that has
	 * rearranged what a polar bear does. A quieted bear keeps the goal and the goal declines to run,
	 * so quieting a bear and then hitting it needs nothing taken off it or put back.
	 */
	public static void makeShy(PolarBear bear) {
		bear.getGoalSelector().addGoal(2, new Shy(bear));
	}

	/** Its own class, so it can be told apart from any other avoiding a polar bear ever does. */
	public static final class Shy extends AvoidEntityGoal<Player> {
		private final PolarBear bear;

		Shy(PolarBear bear) {
			super(bear, Player.class, SHY_DISTANCE, 1.0, 1.3);
			this.bear = bear;
		}

		/** A black bear that has taken food from you has no business running away from you next. */
		@Override
		public boolean canUse() {
			return !isCalm(bear) && super.canUse();
		}

		@Override
		public boolean canContinueToUse() {
			return !isCalm(bear) && super.canContinueToUse();
		}
	}
}
