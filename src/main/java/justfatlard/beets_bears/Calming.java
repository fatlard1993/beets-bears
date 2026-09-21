package justfatlard.beets_bears;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Quieting a bear with something sweet.
 *
 * <p>The mod's whole name is this gesture. Hold out honey or a beetroot and the bear stops meaning
 * you harm - it will not charge you over a cub, and whatever anger it was carrying is let go. It is
 * not taming: a quieted bear does not follow you, wait for you or belong to you. That is
 * better-companions' job, and with that mod installed the same two foods do it, held out while
 * crouching. This is the standing gesture and the smaller promise.
 *
 * <p>Bears only. Honey and beetroot mean nothing to anything else here, and a mod that quietly
 * pacified every animal in the game would be a different mod.
 *
 * <p>The promise is only as good as your own behaviour: hit a quieted bear and it stops being
 * quieted, because an animal that kept trusting you through that would be a toy rather than a bear.
 */
public final class Calming {
	private Calming() {}

	public static boolean calms(ItemStack held) {
		return held.is(Items.HONEY_BOTTLE) || held.is(Items.BEETROOT);
	}

	public static void register() {
		UseEntityCallback.EVENT.register(Calming::onUse);
	}

	private static InteractionResult onUse(Player player, Level level, InteractionHand hand,
			Entity entity, EntityHitResult hit) {
		if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
		if (!(entity instanceof PolarBear bear)) return InteractionResult.PASS;

		// Crouching is better-companions' gesture for keeping an animal. Standing is this one, so
		// the two never fight over the same click even with both mods installed.
		if (player.isShiftKeyDown()) return InteractionResult.PASS;

		ItemStack held = player.getItemInHand(hand);
		if (!calms(held)) return InteractionResult.PASS;
		if (Temper.isCalm(bear)) return InteractionResult.PASS;

		boolean wasBeetroot = held.is(Items.BEETROOT);
		if (!player.isCreative()) {
			// A honey bottle leaves the glass behind, the way drinking one does.
			ItemStack remainder = held.is(Items.HONEY_BOTTLE)
				? new ItemStack(Items.GLASS_BOTTLE) : ItemStack.EMPTY;
			held.shrink(1);
			if (!remainder.isEmpty() && !player.getInventory().add(remainder)) {
				// Hands full: the bottle goes on the ground rather than nowhere.
				level.addFreshEntity(new ItemEntity(level,
					player.getX(), player.getEyeY() - 0.3, player.getZ(), remainder));
			}
		}

		quiet(serverLevel, bear);
		if (wasBeetroot && player instanceof ServerPlayer served) Awards.beetsToABear(served);
		return InteractionResult.SUCCESS;
	}

	/** Let go of everything the bear was holding against anyone, and stop it starting again. */
	private static void quiet(ServerLevel level, PolarBear bear) {
		bear.addTag(Temper.CALM_TAG);
		bear.setTarget(null);
		bear.setPersistentAngerTarget(null);
		bear.setPersistentAngerEndTime(0L);
		// A black bear that has taken food from you has no business running away from you next.
		Temper.unsettle(bear);

		level.sendParticles(ParticleTypes.HEART, bear.getX(), bear.getY() + bear.getBbHeight(),
			bear.getZ(), 5, 0.4, 0.3, 0.4, 0.0);
		level.playSound(null, bear.blockPosition(), SoundEvents.POLAR_BEAR_AMBIENT,
			SoundSource.NEUTRAL, 1.0F, 1.1F);
	}

	/** A bear you hit is not a bear you have quieted. */
	public static void provoked(PolarBear bear) {
		if (!bear.removeTag(Temper.CALM_TAG)) return;
		Temper.settle(bear);
	}
}
