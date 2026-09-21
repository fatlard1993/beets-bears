package justfatlard.beets_bears.mixin;

import justfatlard.beets_bears.Bears;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Where a bear is born decides which bear it is.
 *
 * <p>Here rather than at load, because this fires once, when the animal comes into the world, and
 * loading fires every time a chunk comes back. A polar bear that has wandered south into a forest
 * is a polar bear a long way from home; if the question were asked again each time its chunk
 * loaded, it would turn black on arrival.
 */
@Mixin(Mob.class)
public abstract class BearSpawnMixin {

	@Inject(method = "finalizeSpawn", at = @At("RETURN"))
	private void beetsBears$becomeWild(ServerLevelAccessor level, DifficultyInstance difficulty,
			EntitySpawnReason reason, SpawnGroupData data,
			CallbackInfoReturnable<SpawnGroupData> cir) {
		if (!((Object) this instanceof PolarBear bear)) return;
		Bears.Kind kind = Bears.wildIn(level, bear.blockPosition());
		if (kind != null) Bears.becomeWild(bear, kind);
	}
}
