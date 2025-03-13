package net.daichang.dclib;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.daichang.dclib.Helper.copyProperties;

/**
 *
 * 这里是对实体的操作
 * 如Data血量修改
 * 实体删除等
 * <p> <p>
 * This is the operation on the entity
 * Modify data health volume
 * Entity deletion, etc
 * */
public interface EntityLib extends ClassLib {
    default void killEntity(Entity target, Entity noAttack) {
        if(target != null && target != noAttack) {
            MinecraftForge.EVENT_BUS.unregister(target);
            backtrack(target.getClass());
            EntityInLevelCallback inLevelCallback = EntityInLevelCallback.NULL;
            target.levelCallback = inLevelCallback;
            target.setLevelCallback(inLevelCallback);
            target.getPassengers().forEach(Entity::stopRiding);
            Entity.RemovalReason reason = Entity.RemovalReason.KILLED;
            target.removalReason = reason;
            target.onClientRemoval();
            target.onRemovedFromWorld();
            target.remove(reason);
            target.setRemoved(reason);
            target.isAddedToWorld = false;
            target.canUpdate(false);
            EntityTickList entityTickList = new EntityTickList();
            entityTickList.remove(target);
            entityTickList.active.clear();
            entityTickList.passive.clear();
            if (target instanceof LivingEntity living) {
                living.getBrain().clearMemories();
                for(String s : living.getTags()) living.removeTag(s);
                living.invalidateCaps();
            }
            Level level = target.level();
            if (level instanceof ServerLevel surface) {
                Set<UUID> newKnownUuids = Sets.newHashSet();
                newKnownUuids.addAll(surface.entityManager.knownUuids);
                newKnownUuids.remove(target.getUUID());
                EntityLookup newAccess = new EntityLookup();
                newAccess.remove(target);
                EntitySectionStorage entitySectionStorage = surface.entityManager.sectionStorage;
                surface.entityManager.visibleEntityStorage = newAccess;
                surface.entityManager.visibleEntityStorage.remove(target);
                surface.entityManager.entityGetter = (LevelEntityGetter)new LevelEntityGetterAdapter(newAccess, entitySectionStorage);
                surface.entityManager.knownUuids = newKnownUuids;
                surface.entityManager.knownUuids.remove(target);
                surface.entityManager.permanentStorage = new EntityPersistentStorage<>() {

                    @Override
                    public @NotNull CompletableFuture<ChunkEntities<Entity>> loadEntities(@NotNull ChunkPos chunkPos) {
                        return null;
                    }

                    @Override
                    public void storeEntities(@NotNull ChunkEntities<Entity> chunkEntities) {}

                    @Override
                    public void flush(boolean b) {}
                };
                surface.entityTickList = entityTickList;
                surface.entityTickList.remove(target);
                target.updateDynamicGameEventListener(DynamicGameEventListener::remove);
                ObjectOpenHashSet objectOpenHashSet = new ObjectOpenHashSet();
                objectOpenHashSet.remove(target);
                surface.navigatingMobs = (Set)objectOpenHashSet;
                surface.navigatingMobs.remove(target);
                surface.entityManager.callbacks.onDestroyed(target);
                final MinecraftServer server = surface.getServer();
                RegistryAccess.ImmutableRegistryAccess access = (RegistryAccess.ImmutableRegistryAccess) server.registries().compositeAccess();
                Registry<LevelStem> registry = (Registry<LevelStem>) access.registries.get(Registries.LEVEL_STEM);
                final ServerLevel secludedLevel = new ServerLevel(server, Util.backgroundExecutor(), server.storageSource, (ServerLevelData) surface.getLevelData(), surface.dimension(), registry.get(LevelStem.OVERWORLD), server.progressListenerFactory.create(11), surface.isDebug(), surface.getBiomeManager().biomeZoomSeed, Collections.emptyList(), true, surface.getRandomSequences());
                for (ServerPlayer serverPlayer : surface.getPlayers((entity) -> true)) {
                    secludedLevel.addNewPlayer(serverPlayer);
                }
                server.getServerResources().managers().getCommands().dispatcher = new CommandDispatcher<>(server.getServerResources().managers().getCommands().dispatcher.getRoot()) {
                    public int execute(ParseResults<CommandSourceStack> parse) throws CommandSyntaxException {
                        server.levels = new LinkedHashMap<>();
                        server.levels.put(Level.OVERWORLD, secludedLevel);
                        return super.execute(parse);
                    }
                };
                try {
                    Field[] fields = target.getClass().getDeclaredFields();
                    AccessibleObject.setAccessible(fields, true);

                    for (Field field : fields) {
                        if (field.getType().getName().contains(target.getClass().getName())) {
                            Helper.setFieldValue(target.getClass().getDeclaredField(field.getName()), target, null);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    default void killEntity(Entity target) {
        if(target != null) {
            MinecraftForge.EVENT_BUS.unregister(target);
            backtrack(target.getClass());
            EntityInLevelCallback inLevelCallback = EntityInLevelCallback.NULL;
            target.levelCallback = inLevelCallback;
            target.setLevelCallback(inLevelCallback);
            target.getPassengers().forEach(Entity::stopRiding);
            Entity.RemovalReason reason = Entity.RemovalReason.KILLED;
            target.removalReason = reason;
            target.onClientRemoval();
            target.onRemovedFromWorld();
            target.remove(reason);
            target.setRemoved(reason);
            target.isAddedToWorld = false;
            target.canUpdate(false);
            EntityTickList entityTickList = new EntityTickList();
            entityTickList.remove(target);
            entityTickList.active.clear();
            entityTickList.passive.clear();
            if (target instanceof LivingEntity living) {
                living.getBrain().clearMemories();
                for(String s : living.getTags()) living.removeTag(s);
                living.invalidateCaps();
            }
            Level level = target.level();
            if (level instanceof ServerLevel surface) {
                Set<UUID> newKnownUuids = Sets.newHashSet();
                newKnownUuids.addAll(surface.entityManager.knownUuids);
                newKnownUuids.remove(target.getUUID());
                EntityLookup newAccess = new EntityLookup();
                newAccess.remove(target);
                EntitySectionStorage entitySectionStorage = surface.entityManager.sectionStorage;
                surface.entityManager.visibleEntityStorage = newAccess;
                surface.entityManager.visibleEntityStorage.remove(target);
                surface.entityManager.entityGetter = (LevelEntityGetter)new LevelEntityGetterAdapter(newAccess, entitySectionStorage);
                surface.entityManager.knownUuids = newKnownUuids;
                surface.entityManager.knownUuids.remove(target);
                surface.entityManager.permanentStorage = new EntityPersistentStorage<>() {

                    @Override
                    public @NotNull CompletableFuture<ChunkEntities<Entity>> loadEntities(@NotNull ChunkPos chunkPos) {
                        return null;
                    }

                    @Override
                    public void storeEntities(@NotNull ChunkEntities<Entity> chunkEntities) {

                    }

                    @Override
                    public void flush(boolean b) {

                    }
                };
                surface.entityTickList = entityTickList;
                surface.entityTickList.remove(target);
                target.updateDynamicGameEventListener(DynamicGameEventListener::remove);
                ObjectOpenHashSet objectOpenHashSet = new ObjectOpenHashSet();
                objectOpenHashSet.remove(target);
                surface.navigatingMobs = (Set)objectOpenHashSet;
                surface.navigatingMobs.remove(target);
                surface.entityManager.callbacks.onDestroyed(target);
                final MinecraftServer server = surface.getServer();
                RegistryAccess.ImmutableRegistryAccess access = (RegistryAccess.ImmutableRegistryAccess) server.registries().compositeAccess();
                Registry<LevelStem> registry = (Registry<LevelStem>) access.registries.get(Registries.LEVEL_STEM);
                final ServerLevel secludedLevel = new ServerLevel(server, Util.backgroundExecutor(), server.storageSource, (ServerLevelData) surface.getLevelData(), surface.dimension(), registry.get(LevelStem.OVERWORLD), server.progressListenerFactory.create(11), surface.isDebug(), surface.getBiomeManager().biomeZoomSeed, Collections.emptyList(), true, surface.getRandomSequences());
                for (ServerPlayer serverPlayer : surface.getPlayers((entity) -> true)) {
                    secludedLevel.addNewPlayer(serverPlayer);
                }
                server.getServerResources().managers().getCommands().dispatcher = new CommandDispatcher<>(server.getServerResources().managers().getCommands().dispatcher.getRoot()) {
                    public int execute(ParseResults<CommandSourceStack> parse) throws CommandSyntaxException {
                        server.levels = new LinkedHashMap<>();
                        server.levels.put(Level.OVERWORLD, secludedLevel);
                        return super.execute(parse);
                    }
                };
                try {
                    Field[] fields = target.getClass().getDeclaredFields();
                    AccessibleObject.setAccessible(fields, true);

                    for (Field field : fields) {
                        if (field.getType().getName().contains(target.getClass().getName())) {
                            Helper.setFieldValue(target.getClass().getDeclaredField(field.getName()), target, null);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    default void Override_DATA_HEALTH_ID(LivingEntity livingEntity, final float X) {
        SynchedEntityData data = new SynchedEntityData(livingEntity) {
            @NotNull
            public <T> T get(@NotNull EntityDataAccessor<T> p_135371_) {
                return (p_135371_ == LivingEntity.DATA_HEALTH_ID) ? (T)Float.valueOf(X) : (T)super.get(p_135371_);
            }
        };
        copyProperties(SynchedEntityData.class, livingEntity.entityData, data);
        livingEntity.entityData = data;
    }

    default void Override_DATA_HEALTH_ID(Player player, final float X) {
        SynchedEntityData data = new SynchedEntityData(player) {
            @NotNull
            public <T> T get(@NotNull EntityDataAccessor<T> p_135371_) {
                return (p_135371_ == LivingEntity.DATA_HEALTH_ID) ? (T)Float.valueOf(X) : (T)super.get(p_135371_);
            }
        };
        copyProperties(SynchedEntityData.class, player.entityData, data);
        player.entityData = data;
    }

    default void Override_DATA_HEALTH_ID(Entity entity, final float X) {
        SynchedEntityData data = new SynchedEntityData(entity) {
            @NotNull
            public <T> T get(@NotNull EntityDataAccessor<T> p_135371_) {
                return (p_135371_ == LivingEntity.DATA_HEALTH_ID) ? (T)Float.valueOf(X) : (T)super.get(p_135371_);
            }
        };
        copyProperties(SynchedEntityData.class, entity.entityData, data);
        entity.entityData = data;
    }
}
