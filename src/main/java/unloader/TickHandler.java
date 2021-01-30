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
    private static final Logger logger = LogManager.getLogger(UnloaderMod.MODID);

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

        for (int re : UnloaderMod.config.blacklistDimIDs)
            if (id.equals(re))
                return;

        // could just precompile a hashtable by mixin into DimensionManager#registerDimension
        // so that it works with dynamically generated dimensions i.e. mystcraft
        // performance benefit isn't rly worth it tho
        for (String re : UnloaderMod.config.blacklistDimStrings)
            if (w.provider.getDimensionName().matches(re))
                return;

        if (w.getChunkProvider().getLoadedChunkCount() != 0
                || !w.loadedEntityList.isEmpty()
                || !w.loadedTileEntityList.isEmpty()
                || !ForgeChunkManager.getPersistentChunksFor(w).isEmpty()
                || !w.playerEntities.isEmpty()
                || DimensionManager.shouldLoadSpawn(id)) {
            return;
        }

        try {
            w.saveAllChunks(true, null);
        } catch (MinecraftException e) {
            logger.error("Caught an exception while saving all chunks:", e);
            e.printStackTrace();
        } finally {
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(w));
            w.flush();
            DimensionManager.setWorld(id, null);
        }
    }
}
