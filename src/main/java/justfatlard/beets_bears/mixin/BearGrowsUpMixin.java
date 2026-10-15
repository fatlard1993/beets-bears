package justfatlard.beets_bears.mixin;

import justfatlard.beets_bears.Bears;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A cub and a grown bear are drawn with two different models, each laying its coat out its own way,
 * so the coat has to change on the tick the body does. Left alone, a grown bear wears its cub coat
 * stretched over the wrong boxes until its chunk next reloads.
 */
@Mixin(AgeableMob.class)
public abstract class BearGrowsUpMixin {

	@Inject(method = "ageBoundaryReached", at = @At("TAIL"))
	private void beetsBears$redress(CallbackInfo ci) {
		if ((Object) this instanceof PolarBear bear && !bear.level().isClientSide()) {
			Bears.dress(bear);
		}
	}
}
