package com.mystic.eccf;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(modid = "eccf")
public class EntityUpdateOptimizer {

    // The maximum number of entities in a chunk before optimization is triggered
    private static final int MAX_ENTITIES_PER_CHUNK = 50;
    private Map<ChunkPos, Integer> entityCountMap;
    private Set<Integer> pendingRemovalEntities;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Pre-initialization code for your mod
        entityCountMap = new HashMap<>();
        pendingRemovalEntities = new HashSet<>();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        World world = entity.getEntityWorld();

        if (!world.isRemote && shouldOptimizeEntity(entity)) {
            ChunkPos chunkPos = getChunkPos(entity);
            int entityCount = getEntityCountInChunk(chunkPos);

            if (entityCount > MAX_ENTITIES_PER_CHUNK) {
                entity.setDead(); // Mark the entity for removal without killing it
                pendingRemovalEntities.add(entity.getEntityId());
            } else {
                incrementEntityCount(chunkPos);
            }
        }
    }

    private boolean shouldOptimizeEntity(Entity entity) {
        return !(entity instanceof EntityHanging || entity instanceof EntityEnderCrystal || entity instanceof EntityThrowable || entity instanceof EntityItem);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (WorldServer worldServer : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
                if (!worldServer.isRemote) {
                    for (Integer entityId : pendingRemovalEntities) {
                        Entity entity = worldServer.getEntityByID(entityId);
                        if (entity != null) {
                            ChunkPos chunkPos = getChunkPos(entity);
                            removeEntityFromTracker(entity, worldServer, chunkPos);
                        }
                    }
                }
            }
            pendingRemovalEntities.clear();
        }
    }

    private int getEntityCountInChunk(ChunkPos chunkPos) {
        return entityCountMap.getOrDefault(chunkPos, 0);
    }

    private void incrementEntityCount(ChunkPos chunkPos) {
        int count = getEntityCountInChunk(chunkPos);
        entityCountMap.put(chunkPos, count + 1);
    }

    private void removeEntityFromTracker(Entity entity, WorldServer worldServer, ChunkPos chunkPos) {
        EntityRegistry.EntityRegistration registration = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
        if (registration != null) {
            ChunkProviderServer chunkProvider = worldServer.getChunkProvider();
            Chunk chunk = chunkProvider.getLoadedChunk(chunkPos.x, chunkPos.z);

            if (chunk != null) {
                chunk.removeEntity(entity);
                worldServer.getEntityTracker().untrack(entity);
            }
        }
    }


    private ChunkPos getChunkPos(Entity entity) {
        BlockPos entityPos = entity.getPosition();
        int chunkX = entityPos.getX() >> 4;
        int chunkZ = entityPos.getZ() >> 4;
        return new ChunkPos(chunkX, chunkZ);
    }
}