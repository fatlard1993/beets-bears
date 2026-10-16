package justfatlard.beets_bears;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import justfatlard.pandorical.api.PandoricalApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Two more bears, built on the one the game already has.
 *
 * <p>A black bear in the woods and a brown bear in the conifers, each its own animal running the
 * polar bear's code underneath: resized, recoloured, and given a temper that suits living somewhere
 * people do. See {@link Bears} for how a client that has never heard of them is shown one.
 */
public class Main implements ModInitializer {
	public static final String MOD_ID = "beets-bears-justfatlard";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final String COMPANIONS = "better-companions-justfatlard";
	private static final String BLOCK_TIP = "block-tip";

	@Override
	public void onInitialize() {
		Bears.register();
		Spawns.register();
		Calming.register();

		// The two coats, the cubs' coats, the eggs' pictures and the names.
		PandoricalApi.content().registerModAssets(MOD_ID);

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!(entity instanceof PolarBear bear)) return;
			if (!(entity.level() instanceof ServerLevel level)) return;
			Drops.onDeath(bear, level);
		});

		// Trust is only worth something if it can be broken.
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, dealt, taken, blocked) -> {
			if (!(entity instanceof PolarBear bear)) return;
			if (!(source.getEntity() instanceof Player)) return;
			Calming.provoked(bear);
		});

		// Isolated in their own classes and only reached from here: each names another mod's types
		// directly, so neither must load without that mod.
		if (FabricLoader.getInstance().isModLoaded(COMPANIONS)) {
			justfatlard.beets_bears.integration.CompanionBears.register();
		}
		if (FabricLoader.getInstance().isModLoaded(BLOCK_TIP)) {
			justfatlard.beets_bears.integration.BearTips.register();
		}

		LOGGER.info("[{}] Black and brown bears in the woods; {}",
			MOD_ID, Drops.ours() ? "dropping meat of their own" : "leaving the meat to Let's Cook");
	}
}
