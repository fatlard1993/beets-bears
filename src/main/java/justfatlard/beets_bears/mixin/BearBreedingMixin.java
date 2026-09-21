package justfatlard.beets_bears.mixin;

import justfatlard.beets_bears.Bears;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A bear's cub is the same bear its parent is.
 *
 * <p>A bred cub never goes through the spawn hook - the game makes it and puts it down - so without
 * this a pair of black bears in a forest would raise white cubs, which is the kind of thing nobody
 * reports as a bug and everybody notices.
 */
@Mixin(PolarBear.class)
public abstract class BearBreedingMixin {

	@Inject(method = "getBreedOffspring", at = @At("RETURN"))
	private void beetsBears$inherit(ServerLevel level, AgeableMob mate,
			CallbackInfoReturnable<AgeableMob> cir) {
		if (!(cir.getReturnValue() instanceof PolarBear cub)) return;
		// The mother's kind, falling back to the father's: a mixed pair is not possible today, but
		// reading only one parent would quietly pick wrong the day it is.
		Bears.Kind kind = Bears.of((PolarBear) (Object) this);
		if (kind == null && mate instanceof PolarBear other) kind = Bears.of(other);
		if (kind != null) Bears.becomeWild(cub, kind);
	}
}
