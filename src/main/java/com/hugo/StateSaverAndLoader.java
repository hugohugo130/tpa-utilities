package com.hugo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {

    public HashMap<UUID,PlayerData> players = new HashMap<>();

    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach(((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putString("language",playerData.toString());
            playersNbt.put(uuid.toString(),playerNbt);
        }));
        nbt.put("players", playersNbt);
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        Optional<NbtCompound> playersNbtOpt = tag.getCompound("players");
        if (playersNbtOpt.isPresent()) {
            NbtCompound playersNbt = playersNbtOpt.get();
            playersNbt.getKeys().forEach(key -> {
                Optional<NbtCompound> playerNbtOpt = playersNbt.getCompound(key);
                if (playerNbtOpt.isPresent()) {
                    NbtCompound playerNbt = playerNbtOpt.get();
                    PlayerData playerData = new PlayerData();
                    Optional<String> langOpt = playerNbt.getString("language");
                    if (langOpt.isPresent()) {
                        playerData.setLanguage(langOpt.get());
                    }
                    UUID uuid = UUID.fromString(key);
                    state.players.put(uuid, playerData);
                }
            });
        }
        return state;
    }

    private static final HashMap<MinecraftServer, StateSaverAndLoader> serverStates = new HashMap<>();

    public static StateSaverAndLoader getServerState(MinecraftServer server){
        // 暫時使用內存存儲，不使用持久化
        // TODO: 在找到正確的 Minecraft 1.21.10 API 後恢復持久化功能
        return serverStates.computeIfAbsent(server, k -> new StateSaverAndLoader());
    }

    public static PlayerData getPlayerState(LivingEntity player){
        MinecraftServer server = null;
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            if (serverPlayer.getEntityWorld() instanceof ServerWorld) {
                server = ((ServerWorld) serverPlayer.getEntityWorld()).getServer();
            }
        } else {
            // 對於非玩家實體，嘗試從世界獲取伺服器
            if (player.getEntityWorld() instanceof ServerWorld) {
                server = ((ServerWorld) player.getEntityWorld()).getServer();
            }
        }
        
        if (server == null) {
            return new PlayerData();
        }
        
        StateSaverAndLoader serverState = getServerState(server);
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }

    public static void resetPlayerState(MinecraftServer server){
        StateSaverAndLoader serverState = getServerState(server);
        serverState.players.forEach(((uuid, playerData) -> playerData.setLanguage("zh")));
        saveState(server);
    }

    public static void saveState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        persistentStateManager.save();
    }
}
