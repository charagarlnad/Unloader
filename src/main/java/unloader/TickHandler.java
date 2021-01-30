package unloader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TickHandler {
    private final Logger logger = LogManager.getLogger(UnloaderMod.MODID);

    private int tickCount = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        tickCount++;
        if (tickCount < UnloaderMod.config.unloadInterval) {
            return;
        }
        tickCount = 0;

        Integer[] dims = DimensionManager.getIDs();

        for (Integer id : dims) {
            handleDim(id);
        }
    }

    private void handleDim(Integer id) {
        WorldServer w = DimensionManager.getWorld(id);

        for (String re : UnloaderMod.config.blacklistDims) {
            if (w.provider.getDimensionName().matches(re)) {
                return;
            }
            if (Integer.toString(id).matches(re)) {
                return;
            }
        }

        if (w.getChunkProvider().getLoadedChunkCount() != 0) {
            return;
        }
        if (!w.loadedEntityList.isEmpty()) {
            return;
        }
        if (!w.loadedTileEntityList.isEmpty()) {
            return;
        }
        if (!ForgeChunkManager.getPersistentChunksFor(w).isEmpty()) {
            return;
        }
        if (!w.playerEntities.isEmpty()) {
            return;
        }
        if (DimensionManager.shouldLoadSpawn(id)) {
            return;
        }

        try {
            w.saveAllChunks(true, null);
        } catch (MinecraftException e) {
            logger.error("Caught an exception while saving all chunks:", e);
        } finally {
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(w));
            w.flush();
            DimensionManager.setWorld(id, null);
        }
    }
}
