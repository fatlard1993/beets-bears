package justfatlard.beets_bears.gametest;

import java.util.ArrayList;
import java.util.List;

import justfatlard.beets_bears.Bears;
import justfatlard.pandorical.api.PandoricalApi;
import justfatlard.pandorical.gametest.Pictures;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.phys.Vec3;

/**
 * The three bears side by side, for the eye: a polar bear, a black bear and a brown bear, each with
 * a cub in front of it, on a stone floor in daylight. Taken from the side, from three-quarters in
 * front, each one close from the front and from three-quarters, with the grown bears up on their
 * hind legs, which is where a body that did not fit the polar bear's animation would come apart,
 * and once at dusk, where a black bear that was only a black shape would vanish.
 *
 * <p>The shots land in build/run/clientGameTest/screenshots, and with {@code -Ppictures} in this
 * repo's screenshots/ too.
 */
public final class BearsPictured implements FabricClientGameTest {

	/** Where each grown bear stands along the row, from the polar bear's side; the brown bear needs the most room. */
	private static final double[] ALONG = {-4.0, 0.4, 4.8};
	/** How far in front of its parent each cub stands, toward the camera. */
	private static final double CUB_AHEAD = 2.6;

	private record Bear(int id, boolean cub, double x, double z) {}

	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			TestServerContext server = world.getServer();
			TestServerConnection connection = world.getConnection();
			connection.waitForChunksRender();
			server.waitFor(s -> PandoricalApi.isContentReady(connection.getServerPlayer()));
			Pictures.stage(context, server, 6000);
			// A narrow lens, so the row fills the frame from far enough back to see it whole.
			context.runOnClient(client -> client.options.fov().set(30));

			BlockPos base = server.computeOnServer(s -> connection.getServerPlayer().blockPosition());
			int x = base.getX(), y = base.getY(), z = base.getZ();
			// A plain floor, and nothing behind the bears but sky.
			server.runCommand("fill %d %d %d %d %d %d minecraft:smooth_stone".formatted(x - 14, y - 1, z - 10, x + 14, y - 1, z + 20));

			List<Bear> bears = server.computeOnServer(s -> {
				ServerLevel level = s.overworld();
				List<EntityType<? extends PolarBear>> types = List.of(EntityTypes.POLAR_BEAR, Bears.BLACK_BEAR, Bears.BROWN_BEAR);
				List<Bear> out = new ArrayList<>();
				for (int i = 0; i < types.size(); i++) {
					for (boolean cub : new boolean[] {false, true}) {
						PolarBear bear = types.get(i).create(level, EntitySpawnReason.COMMAND);
						// A cub a little ahead of its parent, the way one walks in front of its mother.
						double bx = x + 0.5 + ALONG[i] + (cub ? -1.3 : 0);
						double bz = z + 0.5 + (cub ? CUB_AHEAD : 0);
						bear.snapTo(bx, y, bz, 90F, 0F);
						bear.setNoAi(true);
						bear.setPersistenceRequired();
						bear.setSilent(true);
						if (cub) bear.setAge(-24000);
						level.addFreshEntity(bear);
						out.add(new Bear(bear.getId(), cub, bx, bz));
					}
				}
				return out;
			});
			context.waitFor(client -> bears.stream().allMatch(b -> client.level.getEntity(b.id()) != null));

			Vec3 centre = new Vec3(x + 0.5 + 0.5, y, z + 0.5 + CUB_AHEAD / 2);

			// Side on: every bear facing left, the camera square to the row and a little above it, so
			// the cubs in front stand below their parents in the frame rather than across them.
			turn(server, bears, 90F);
			Pictures.look(server, centre.add(0, 1.6, 16), centre.add(0, 0.8, 0));
			shoot(context, connection, "bears-side");

			// Three-quarters from the front.
			turn(server, bears, 40F);
			Pictures.look(server, centre.add(0, 1.2, 16), centre.add(0, 0.8, 0));
			shoot(context, connection, "bears-three-quarter");

			// Each bear close: square on from the front at about its own eye height, then from
			// three-quarters, so the face, the ears and the coat can be seen.
			String[] names = {"polar", "black", "brown"};
			for (int i = 0; i < 3; i++) {
				Vec3 at = new Vec3(x + 0.5 + ALONG[i] - 0.2, y, z + 0.5 + CUB_AHEAD / 2);
				double back = i == 2 ? 9 : i == 1 ? 7 : 8;
				turn(server, bears, 0F);
				Pictures.look(server, at.add(0, -0.7, back), at.add(0, 0.9, 0));
				shoot(context, connection, "bears-front-" + names[i]);
				double angle = Math.toRadians(40);
				Pictures.look(server, at.add(-Math.sin(angle) * back, -0.2, Math.cos(angle) * back), at.add(0, 0.8, 0));
				shoot(context, connection, "bears-close-" + names[i]);
			}

			// Up on their hind legs, as a polar bear stands to warn and to strike.
			turn(server, bears, 55F);
			server.runOnServer(s -> {
				for (Bear b : bears) {
					if (!b.cub() && s.overworld().getEntity(b.id()) instanceof PolarBear bear) bear.setStanding(true);
				}
			});
			context.waitTicks(20);
			Pictures.look(server, centre.add(0, 0.6, 16), centre.add(0, 1.3, 0));
			shoot(context, connection, "bears-standing");
			turn(server, bears, 90F);
			Pictures.look(server, centre.add(0, 0.6, 16), centre.add(0, 1.3, 0));
			shoot(context, connection, "bears-standing-side");
			// Each standing bear close and square from the side, from behind the row so no cub is in
			// the way: where every part lands in the pose, against the polar bear's.
			for (int i = 0; i < 3; i++) {
				Vec3 at = new Vec3(x + 0.5 + ALONG[i], y, z + 0.5);
				double back = i == 2 ? 11 : i == 0 ? 9.5 : 7.5;
				Pictures.look(server, at.add(0, 0.2, -back), at.add(0, i == 1 ? 1.2 : 1.6, 0));
				shoot(context, connection, "bears-standing-close-" + names[i]);
			}

			// Back down, at dusk: a black bear has to keep its shape where the light is poor.
			server.runOnServer(s -> {
				for (Bear b : bears) {
					if (s.overworld().getEntity(b.id()) instanceof PolarBear bear) bear.setStanding(false);
				}
			});
			turn(server, bears, 40F);
			server.runCommand("time set 12800");
			Pictures.look(server, centre.add(0, 1.2, 16), centre.add(0, 0.8, 0));
			shoot(context, connection, "bears-dusk");
		}
		context.waitTicks(10);
	}

	/** Every bear facing one way, body and head together, where it stands. */
	private static void turn(TestServerContext server, List<Bear> bears, float yaw) {
		server.runOnServer(s -> {
			for (Bear b : bears) {
				if (!(s.overworld().getEntity(b.id()) instanceof PolarBear bear)) continue;
				bear.snapTo(b.x(), bear.getY(), b.z(), yaw, 0F);
				bear.setYHeadRot(yaw);
				bear.setYBodyRot(yaw);
			}
		});
	}

	private static void shoot(ClientGameTestContext context, TestServerConnection connection, String name) {
		context.waitTicks(15);
		var shot = Pictures.shoot(context, connection, name);
		System.out.println("[beets-bears test] picture " + shot.toAbsolutePath());
	}
}
