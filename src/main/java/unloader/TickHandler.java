package unloader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

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

        //System.out.println("peggers list: " + Arrays.toString(dims));

        for (Integer id : dims) {
            handleDim(id);
        }
    }

    private void handleDim(Integer id) {
        WorldServer w = DimensionManager.getWorld(id);
        String dimName = w.getProviderName();

        for (String re : UnloaderMod.config.blacklistDims) {
            if (dimName.matches(re)) {
                return;
            }
            if (Integer.toString(id).matches(re)) {
                return;
            }
        }

        if (DimensionManager.shouldLoadSpawn(id)) {
            System.out.println("spawn");
            return;
        }

        IChunkProvider p = w.getChunkProvider();
        if (p.getLoadedChunkCount() != 0) {
            System.out.println("loaded chunks");
            return;
        }
        if (!ForgeChunkManager.getPersistentChunksFor(w).isEmpty()) {
            System.out.println("persistent chunks");
            return;
        }

        if (!w.playerEntities.isEmpty()) {
            System.out.println("player entities");
            return;
        }
        if (!w.loadedEntityList.isEmpty()) {
            System.out.println("loaded entities");
            return;
        }
        if (!w.loadedTileEntityList.isEmpty()) {
            System.out.println("tile entities");
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
            //DimensionManager.setWorld(id, null, w.func_73046_m());
        }
    }
}
