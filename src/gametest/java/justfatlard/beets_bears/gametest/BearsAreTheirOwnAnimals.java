package justfatlard.beets_bears.gametest;

import java.util.List;
import java.util.UUID;

import justfatlard.beets_bears.Bears;
import justfatlard.beets_bears.ForestBear;
import justfatlard.pandorical.entitykind.EntityKinds;
import justfatlard.pandorical.gametest.Smoke;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * What the readme now promises: a black bear and a brown bear are each their own animal, at their
 * own size and health however they arrive, named for what they are, with cubs of their own kind and
 * an egg of their own; a vanilla client is shown a polar bear in their place; and a bear saved by
 * 1.0.x as a tagged polar bear comes back as the right one, once, with nothing about it lost.
 */
public final class BearsAreTheirOwnAnimals implements FabricClientGameTest {

	/** A polar bear's, which the game sets and this mod reads rather than repeats. */
	private static final float POLAR_HEALTH = 30F;

	@Override
	public void runTest(ClientGameTestContext context) {
		Smoke.run(context, "beets-bears-justfatlard", session -> {
			UUID[] seen = new UUID[1];
			session.onServer(server -> {
				ServerLevel level = (ServerLevel) session.player().level();
				Vec3 here = session.player().position();

				for (Bears.Kind kind : Bears.Kind.values()) {
					EntityType<ForestBear> type = kind.type();
					check(type.getWidth() == EntityTypes.POLAR_BEAR.getWidth()
						&& type.getHeight() == EntityTypes.POLAR_BEAR.getHeight(),
						kind + " is not registered at the polar bear's size");

					// Spawned the way a natural spawn or an egg does it.
					ForestBear bear = type.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
					bear.snapTo(here.add(4, 0, kind.ordinal() * 3));
					bear.finalizeSpawn(level, level.getCurrentDifficultyAt(bear.blockPosition()),
						EntitySpawnReason.SPAWN_ITEM_USE, null);
					level.addFreshEntity(bear);
					sized(bear, kind, "spawned");
					check(bear.getHealth() == bear.getMaxHealth(), kind + " spawned hurt: " + bear.getHealth());
					check(bear.kind() == kind && Bears.of(bear) == kind, kind + " reads as " + Bears.of(bear));
					check(kind.displayName().equals(bear.getName().getString()),
						kind + " is called " + bear.getName().getString());
					Component name = bear.getType().getDescription();
					check(bear.getName().getContents() instanceof TranslatableContents translatable
						&& kind.displayName().equals(translatable.getFallback()),
						kind + " has no plain word for a vanilla client: " + bear.getName());
					check(!name.getString().isEmpty(), kind + " has no type name");

					// Cubs are the parent's kind, and two kinds do not mix.
					check(bear.getBreedOffspring(level, bear) instanceof ForestBear cub && cub.getType() == type,
						kind + "'s cub is " + bear.getBreedOffspring(level, bear));
					ForestBear cub = type.create(level, EntitySpawnReason.BREEDING);
					cub.setBaby(true);
					// The scale is the kind's at every age; a cub's smallness is the polar bear's own.
					sized(cub, kind, "newborn");
					cub.setAge(0);
					sized(cub, kind, "grown cub");
					cub.discard();

					// Its own egg, which makes it.
					check(SpawnEggItem.spawnsEntity(new ItemStack(kind.egg()), type), kind + "'s egg hatches something else");
					check(BuiltInRegistries.ITEM.getKey(kind.egg()).getPath().equals(kind.id() + "_spawn_egg"),
						kind + "'s egg is filed as " + BuiltInRegistries.ITEM.getKey(kind.egg()));

					if (kind == Bears.Kind.BLACK) seen[0] = bear.getUUID();
				}

				ForestBear black = (ForestBear) level.getEntity(seen[0]);
				ForestBear brown = Bears.BROWN_BEAR.create(level, EntitySpawnReason.COMMAND);
				black.setAge(0);
				brown.setAge(0);
				black.setInLove(null);
				brown.setInLove(null);
				check(!black.canMate(brown), "a black bear and a brown bear can breed");
				brown.discard();

				// The polar bear is the game's again: not renamed, not resized.
				PolarBear polar = EntityTypes.POLAR_BEAR.create(level, EntitySpawnReason.COMMAND);
				check("Polar Bear".equals(polar.getName().getString()), "a polar bear is called " + polar.getName().getString());
				check(polar.getScale() == 1F && polar.getMaxHealth() == POLAR_HEALTH, "a polar bear has been resized");
				check(Bears.of(polar) == null, "a polar bear reads as one of ours");
				polar.discard();

				// A vanilla client is shown a polar bear in its place; a Pandorical client is shown the bear.
				ServerPlayer player = session.player();
				ClientboundAddEntityPacket add = new ClientboundAddEntityPacket(black.getId(), black.getUUID(),
					black.getX(), black.getY(), black.getZ(), 0F, 0F, black.getType(), 0, Vec3.ZERO, 0);
				EntityKinds.understood(player.getUUID(), false);
				try {
					check(EntityKinds.mayShow(black, player), "a vanilla client was refused a black bear");
					check(EntityKinds.forPlayer(add, player).getType() == EntityTypes.POLAR_BEAR,
						"a vanilla client was sent " + EntityKinds.forPlayer(add, player).getType());
				} finally {
					EntityKinds.understood(player.getUUID(), true);
				}
				check(EntityKinds.forPlayer(add, player).getType() == Bears.BLACK_BEAR,
					"a Pandorical client was sent " + EntityKinds.forPlayer(add, player).getType());

				// Made by /summon with data of its own, which never goes near a spawn hook.
				server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(),
					"summon beets-bears-justfatlard:brown_bear " + here.x + " " + here.y + " " + (here.z + 8)
						+ " {Tags:[\"summoned\"],CustomName:\"Ursula\"}");
				List<ForestBear> summoned = level.getEntitiesOfClass(ForestBear.class,
					session.player().getBoundingBox().inflate(16), each -> each.entityTags().contains("summoned"));
				check(summoned.size() == 1, summoned.size() + " summoned brown bears");
				sized(summoned.getFirst(), Bears.Kind.BROWN, "summoned");

				legacy(level, here, Bears.Kind.BLACK, false);
				legacy(level, here, Bears.Kind.BROWN, true);
			});

			// Drawn by the polar bear's renderer, under the bear's own type.
			session.waitTicks(10);
			String drawn = session.context().computeOnClient(client -> {
				for (Entity each : client.level.entitiesForRendering()) {
					if (!each.getUUID().equals(seen[0])) continue;
					var renderer = client.getEntityRenderDispatcher().getRenderer(each);
					return (renderer == null ? "nothing" : renderer.getClass().getSimpleName()) + " for "
						+ BuiltInRegistries.ENTITY_TYPE.getKey(each.getType());
				}
				return "no bear on the client";
			});
			check(drawn.equals("PolarBearRenderer for beets-bears-justfatlard:black_bear"), "the client drew " + drawn);
		});
	}

	/** Its size and its health are the kind's, by the same factor. */
	private static void sized(ForestBear bear, Bears.Kind kind, String how) {
		check(Math.abs(bear.getScale() - kind.scale()) < 0.001F, how + " " + kind + " is scale " + bear.getScale());
		check(Math.abs(bear.getMaxHealth() - POLAR_HEALTH * kind.scale()) < 0.01F,
			how + " " + kind + " has max health " + bear.getMaxHealth());
	}

	/**
	 * A bear exactly as 1.0.x saved one, read back the way a chunk reads it: a polar bear with the
	 * tag, the size modifier, hurt, named, quieted, and maybe a cub.
	 */
	private static void legacy(ServerLevel level, Vec3 here, Bears.Kind kind, boolean cub) {
		PolarBear old = EntityTypes.POLAR_BEAR.create(level, EntitySpawnReason.COMMAND);
		old.snapTo(here.add(-4, 0, kind.ordinal() * 3));
		old.addTag(kind.id());
		old.addTag("calm");
		AttributeModifier size = new AttributeModifier(Identifier.fromNamespaceAndPath("beets-bears-justfatlard",
			"size_" + kind.id()), kind.scale() - 1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		for (var attribute : List.of(Attributes.SCALE, Attributes.MAX_HEALTH)) {
			old.getAttribute(attribute).addPermanentModifier(size);
		}
		old.setHealth(old.getMaxHealth() / 2F);
		old.setCustomName(Component.literal("Old " + kind.displayName()));
		if (cub) old.setAge(-24000);
		check(Math.abs(old.getScale() - kind.scale()) < 0.01F, "the 1.0.x bear was not built right");
		float health = old.getHealth();
		UUID uuid = old.getUUID();

		TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
		check(old.save(out), "a 1.0.x bear would not save");
		CompoundTag saved = out.buildResult();
		old.discard();

		Entity back = EntityType.loadEntityRecursive(
			TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), saved),
			level, EntitySpawnReason.LOAD, EntityProcessor.NOP);
		check(back instanceof ForestBear, "a 1.0.x " + kind + " came back as " + (back == null ? "nothing" : back.getType()));
		ForestBear bear = (ForestBear) back;
		check(bear.getType() == kind.type(), "a 1.0.x " + kind + " came back as " + bear.getType());
		check(bear.getUUID().equals(uuid), "a 1.0.x bear came back as somebody else");
		check(bear.isBaby() == cub, "a 1.0.x bear came back " + (cub ? "grown" : "a cub"));
		check(!bear.entityTags().contains(kind.id()), "a 1.0.x bear kept its old tag");
		check(bear.entityTags().contains("calm"), "a 1.0.x bear forgot it had been quieted");
		check(("Old " + kind.displayName()).equals(bear.getCustomName() == null ? null : bear.getCustomName().getString()),
			"a 1.0.x bear lost its name");
		for (var attribute : List.of(Attributes.SCALE, Attributes.MAX_HEALTH)) {
			AttributeInstance instance = bear.getAttribute(attribute);
			check(instance.getModifier(size.id()) == null, "a 1.0.x bear kept its old size modifier on " + attribute);
		}
		check(Math.abs(bear.getMaxHealth() - POLAR_HEALTH * kind.scale()) < 0.01F,
			"a 1.0.x " + kind + " came back with max health " + bear.getMaxHealth());
		check(Math.abs(bear.getScale() - kind.scale()) < 0.01F,
			"a 1.0.x " + kind + " came back at scale " + bear.getScale());
		check(Math.abs(bear.getHealth() - health) < 0.01F, "a 1.0.x bear came back with " + bear.getHealth()
			+ " health, not " + health);
		check(level.addFreshEntity(bear), "a 1.0.x bear would not go back into the world");
		bear.setAge(0);
		check(Math.abs(bear.getScale() - kind.scale()) < 0.01F, "a 1.0.x cub grew up to scale " + bear.getScale());
	}

	private static void check(boolean condition, String failure) {
		if (!condition) throw new AssertionError(failure);
	}
}
