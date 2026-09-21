package justfatlard.beets_bears.mixin;

import justfatlard.beets_bears.Temper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The one place a bear decides to come after somebody.
 *
 * <p>Every route into a fight ends here - the goal that guards cubs, the anger a player earned, the
 * bear answering a hit - so refusing the target refuses all of them at once, and refusing none of
 * the ones {@link Temper#mayHunt} allows. Cheaper and far steadier than taking the polar bear's
 * goal list apart, which would have to be rebuilt every time Mojang rearranges it.
 */
@Mixin(Mob.class)
public abstract class MobTargetMixin {

	@Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
	private void beetsBears$respectQuiet(LivingEntity target, CallbackInfo ci) {
		if (target == null) return;
		if (!((Object) this instanceof PolarBear bear)) return;
		if (!Temper.mayHunt(bear, target)) ci.cancel();
	}
}
