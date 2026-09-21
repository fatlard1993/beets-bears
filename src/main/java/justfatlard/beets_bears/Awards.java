package justfatlard.beets_bears;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

/** The advancement this mod hands out, by the code-awarded {@code impossible} idiom. */
public final class Awards {
	private Awards() {}

	/** A beetroot, handed to a bear. There is only one joke here and this is it. */
	public static void beetsToABear(@Nullable ServerPlayer player) {
		award(player, "battlestar_galactica");
	}

	private static void award(@Nullable ServerPlayer player, String path) {
		if (player == null || player.level().getServer() == null) return;
		AdvancementHolder holder = player.level().getServer().getAdvancements()
			.get(Identifier.fromNamespaceAndPath(Main.MOD_ID, path));
		if (holder == null) return;

		AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
		if (progress.isDone()) return;
		for (String criterion : progress.getRemainingCriteria()) {
			player.getAdvancements().award(holder, criterion);
		}
	}
}
