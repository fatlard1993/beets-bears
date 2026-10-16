package justfatlard.beets_bears;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;

/**
 * A black or a brown bear: which one is its entity type, and everything else is a polar bear's.
 *
 * <p>It stands on its hind legs, swims, panics in fire and raises cubs exactly as a polar bear,
 * because it runs the polar bear's own code. That is also the limit on what it may add: Pandorical
 * clients decode this animal's synced data as a polar bear's, so it adds fields, goals and
 * attributes freely and never an {@code EntityDataAccessor}.
 */
public class ForestBear extends PolarBear {

	public ForestBear(EntityType<? extends ForestBear> type, Level level) {
		super(type, level);
	}

	public Bears.Kind kind() {
		return this.getType() == Bears.BLACK_BEAR ? Bears.Kind.BLACK : Bears.Kind.BROWN;
	}

	/**
	 * A polar bear's, resized: the scale against a polar bear, and its health by the same factor.
	 *
	 * <p>Health rides on the size for the same reason a bigger animal is harder to kill, and because
	 * a brown bear that was only visually larger would be a lie the first time one fought back.
	 */
	static AttributeSupplier.Builder createAttributes(Bears.Kind kind) {
		double health = PolarBear.createAttributes().build().getBaseValue(Attributes.MAX_HEALTH);
		return PolarBear.createAttributes()
			.add(Attributes.SCALE, kind.scale())
			.add(Attributes.MAX_HEALTH, health * kind.scale());
	}

	/**
	 * A black bear's nerves, on top of everything a polar bear does. Added here, once, as the animal
	 * is built, so it needs no re-adding when the animal loads; whether it is calm is the goal's own
	 * question, asked each time it might run.
	 */
	@Override
	protected void registerGoals() {
		super.registerGoals();
		if (this.kind() == Bears.Kind.BLACK) Temper.makeShy(this);
	}

	/** A cub is the same bear its parent is. */
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
		return this.kind().type().create(level, EntitySpawnReason.BREEDING);
	}

	/** Only with its own kind. The game checks the class, and the two bears share one. */
	@Override
	public boolean canMate(Animal partner) {
		return partner.getType() == this.getType() && super.canMate(partner);
	}

	/**
	 * The word, for a client that does not have this mod's names: a vanilla one reads it in a death
	 * message, and without a fallback would read the bare translation key.
	 */
	@Override
	protected Component getTypeName() {
		return Component.translatableWithFallback(this.getType().getDescriptionId(), this.kind().displayName());
	}

	@Override
	public void load(ValueInput input) {
		super.load(input);
		Legacy.shed(this);
	}
}
