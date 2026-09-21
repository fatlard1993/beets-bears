package justfatlard.beets_bears;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * What a bear is worth killing for.
 *
 * <p>Only when Let's Cook is absent. That mod already butchers a polar bear properly - a lean cut
 * with some fat on it, sized by looting, cooked if the animal burned - and all three bears here are
 * polar bears, so its work covers them without being asked. Two mods both deciding what a bear
 * drops would mean twice the meat and no way to tell which half came from where.
 *
 * <p>Without it, vanilla gives a bear nothing but the fish in its stomach, which makes a forest bear
 * a fight with no reason to take it. So this puts meat on the animal, and only as much as the animal
 * is big: the brown bear is the larger one and it says so on the ground.
 */
public final class Drops {
	private Drops() {}

	private static final boolean LETS_COOK =
		FabricLoader.getInstance().isModLoaded("lets-cook-justfatlard");

	/** Whether this mod is the one putting meat on bears in this install. */
	public static boolean ours() { return !LETS_COOK; }

	public static void onDeath(PolarBear bear, ServerLevel level) {
		if (!ours()) return;
		Bears.Kind kind = Bears.of(bear);
		if (kind == null || bear.isBaby()) return;

		int count = switch (kind) {
			case BLACK -> 1 + bear.getRandom().nextInt(2);
			case BROWN -> 2 + bear.getRandom().nextInt(2);
		};
		bear.spawnAtLocation(level, new ItemStack(Items.BEEF, count));
	}
}
