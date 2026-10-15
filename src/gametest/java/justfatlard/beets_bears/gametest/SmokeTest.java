package justfatlard.beets_bears.gametest;

import justfatlard.pandorical.gametest.Smoke;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.polarbear.PolarBear;

/**
 * This mod loads, its mixins bind, and a world comes up with it installed.
 *
 * <p>Getting there proves most of it: a mixin that no longer applies refuses the mod outright, a
 * registration that throws takes the client down, and a datapack that will not parse fails the
 * world. On top of that, one thing only a loaded world can show: a black bear coming back from its
 * save is still shy. Spawned already wearing the tags a saved bear carries, which is exactly what
 * a bear coming back from disk looks like to this mod.
 */
public final class SmokeTest implements FabricClientGameTest {

	@Override
	public void runTest(ClientGameTestContext context) {
		Smoke.run(context, "beets-bears-justfatlard", session -> {
			session.onServer(server -> {
				var level = session.player().level();
				PolarBear bear = EntityTypes.POLAR_BEAR.create(level, EntitySpawnReason.COMMAND);
				bear.addTag("black_bear");
				bear.addTag("shy_installed");
				bear.snapTo(session.player().position().add(4, 0, 0));
				level.addFreshEntity(bear);
				long shy = bear.getGoalSelector().getAvailableGoals().stream()
					.filter(wrapped -> wrapped.getGoal() instanceof AvoidEntityGoal<?>).count();
				if (shy != 1) throw new AssertionError("a reloaded black bear has " + shy + " shy goals, not 1");
			});
		});
	}
}
