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
	 * Give a black bear its nerves, once, as it loads.
	 *
	 * <p>Only an added goal, never a removed one: adding is safe against a version that has
	 * rearranged what a polar bear does, and this runs on every load, so it has to be harmless to
	 * do twice. A goal added twice is ticked twice and flees the same direction, which is why the
	 * tag is checked rather than the goal list.
	 */
	public static void settle(PolarBear bear) {
		if (Bears.of(bear) != Bears.Kind.BLACK) return;
		if (!bear.addTag(SETTLED_TAG)) return;
		bear.getGoalSelector().addGoal(2,
			new AvoidEntityGoal<>(bear, Player.class, SHY_DISTANCE, 1.0, 1.3));
	}

	/**
	 * Marks a bear whose nerves are already installed.
	 *
	 * <p>Rides on the entity rather than a set held here because the alternative is a set of every
	 * bear the server has ever loaded, which never shrinks, and because a bear reloading must not
	 * collect a second copy of the goal.
	 */
	private static final String SETTLED_TAG = "shy_installed";

	/** Dropped when a black bear is quieted, so an owner's bear is not also fleeing from them. */
	public static void unsettle(PolarBear bear) {
		bear.removeTag(SETTLED_TAG);
		bear.getGoalSelector().removeAllGoals(goal -> goal instanceof AvoidEntityGoal<?>);
	}
}
