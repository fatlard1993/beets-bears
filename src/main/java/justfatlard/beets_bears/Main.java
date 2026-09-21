package justfatlard.beets_bears;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import justfatlard.pandorical.api.PandoricalApi;
import justfatlard.pandorical.api.VanillaItemOverride;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Two more bears, out of the one the game already has.
 *
 * <p>A black bear in the woods and a brown bear in the conifers, both of them real polar bears
 * underneath: resized, recoloured, and given a temper that suits living somewhere people do. See
 * {@link Bears} for why they are not a new animal, which is the one decision everything else here
 * follows from.
 */
public class Main implements ModInitializer {
	public static final String MOD_ID = "beets-bears-justfatlard";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final String COMPANIONS = "better-companions-justfatlard";
	private static final String BLOCK_TIP = "block-tip";

	@Override
	public void onInitialize() {
		Spawns.register();
		Calming.register();

		// The coats. Nothing else in here needs a client, but the client has to be handed the two
		// textures or both bears arrive wearing the polar bear's.
		PandoricalApi.content().registerModAssets(MOD_ID);
		renameTheBear();

		// A bear's size is saved with the bear; its coat is not, so every load says it again.
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (!(entity instanceof PolarBear bear)) return;
			Bears.dress(bear);
			Temper.settle(bear);
		});

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

	/**
	 * One egg, three bears.
	 *
	 * <p>The egg now hatches whichever bear the place calls for, so calling it a polar bear egg is
	 * simply wrong two times out of three; and the animal is introduced as a Polar Bear in death
	 * messages and anywhere else the game says its name, which is wrong in the same way. Both
	 * become the plain word, and block-tip puts the precise one back on the card where a player is
	 * actually asking which bear this is.
	 *
	 * <p>Through Pandorical's own two doors rather than an {@code assets/minecraft/lang} file of
	 * ours. Those are registered whole and by path, so the second mod to ship one silently replaces
	 * the first - and Let's Cook already ships one, renaming every meat in the game.
	 */
	private void renameTheBear() {
		PandoricalApi.content().overrideVanillaItem("minecraft:polar_bear_spawn_egg",
			new VanillaItemOverride().name("Bear Spawn Egg"));
		PandoricalApi.contentRegistry().addLangEntries(
			java.util.Map.of("entity.minecraft.polar_bear", "Bear"));
	}
}
