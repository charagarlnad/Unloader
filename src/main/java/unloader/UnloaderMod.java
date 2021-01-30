package unloader;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

import java.io.File;

@Mod(
        modid = UnloaderMod.MODID,
        name = UnloaderMod.NAME,
        version = "@VERSION@",
        acceptableRemoteVersions = "*")
public class UnloaderMod {
    public static final String MODID = "unloader";
    public static final String NAME = "Unloader";

    public static UnloaderConfig config;

    private TickHandler handler = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new UnloaderConfig(new File(event.getModConfigurationDirectory(), "unloader.cfg"));
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        handler = new TickHandler();
        FMLCommonHandler.instance().bus().register(handler);
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        FMLCommonHandler.instance().bus().unregister(handler);
        handler = null;
    }
}
