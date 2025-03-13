package net.daichang.dclib.libs;

import com.google.common.collect.Iterables;
import net.daichang.dclib.EntityLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface LevelLib extends EntityLib {
    default void killLevelAllEntity(ServerLevel level) {
        Iterables.unmodifiableIterable(level.getAllEntities()).forEach(this::killEntity);
        for (Entity entity : level.getAllEntities()) killEntity(entity);
    }

    default Iterator<String> getLevelEntites() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerPlayer serverPlayer = null;
        if (localPlayer != null) {
            serverPlayer = server.getPlayerList().getPlayer(localPlayer.getUUID());
        }
        ServerLevel serverLevel = null;
        if (serverPlayer != null) {
            serverLevel = serverPlayer.serverLevel();
        }
        List<String> entityList = new ArrayList<>();
        assert serverLevel != null;
        Iterables.unmodifiableIterable(serverLevel.getAllEntities()).forEach(entity -> entityList.add(entity.getDisplayName().getString()));
        return entityList.iterator();
    }
}
