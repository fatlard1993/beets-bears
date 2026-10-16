package justfatlard.beets_bears.mixin;

import java.util.Optional;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import justfatlard.beets_bears.Legacy;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** TEMPORARY: a 1.0.x bear, saved as a tagged polar bear, is read back as its own type. See {@link Legacy}. */
@Mixin(EntityType.class)
public abstract class LegacyBearMixin {

	@ModifyReturnValue(method = "by(Lnet/minecraft/world/level/storage/ValueInput;)Ljava/util/Optional;", at = @At("RETURN"))
	private static Optional<EntityType<?>> beetsBears$retype(Optional<EntityType<?>> type,
			@Local(argsOnly = true) ValueInput input) {
		return Legacy.retype(type, input);
	}
}
