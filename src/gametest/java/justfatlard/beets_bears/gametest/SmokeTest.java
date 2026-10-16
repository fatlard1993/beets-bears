package justfatlard.beets_bears.gametest;

import justfatlard.beets_bears.Bears;
import justfatlard.beets_bears.ForestBear;
import justfatlard.beets_bears.Temper;
import justfatlard.pandorical.gametest.Smoke;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.world.entity.EntitySpawnReason;

/**
 * This mod loads, its mixins bind, and a world comes up with it installed.
 *
 * <p>Getting there proves most of it: a mixin that no longer applies refuses the mod outright, a
 * registration that throws takes the client down, and a datapack that will not parse fails the
 * world. On top of that, the one thing about temper a loaded world can show: a black bear is shy,
 * once and not twice, and a brown bear is not shy at all.
 */
public final class SmokeTest implements FabricClientGameTest {

	@Override
	public void runTest(ClientGameTestContext context) {
		Smoke.run(context, "beets-bears-justfatlard", session -> {
			session.onServer(server -> {
				var level = session.player().level();
				for (Bears.Kind kind : Bears.Kind.values()) {
					ForestBear bear = kind.type().create(level, EntitySpawnReason.COMMAND);
					bear.snapTo(session.player().position().add(4, 0, kind.ordinal() * 3));
					level.addFreshEntity(bear);
					long shy = bear.getGoalSelector().getAvailableGoals().stream()
						.filter(wrapped -> wrapped.getGoal() instanceof Temper.Shy).count();
					long expected = kind == Bears.Kind.BLACK ? 1 : 0;
					if (shy != expected) throw new AssertionError("a " + kind + " bear has " + shy + " shy goals, not " + expected);
				}
			});
		});
	}
}
