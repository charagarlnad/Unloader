package unloader;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class UnloaderConfig {
    private final Configuration config;

    public int[] blacklistDimIDs;
    public String[] blacklistDimStrings;
    public int unloadInterval;

    public UnloaderConfig(File file) {
        config = new Configuration(file);

        Property propBlacklistDimIDs = config.get(Configuration.CATEGORY_GENERAL, "blacklistDimIDs", new int[]{0});
        propBlacklistDimIDs.comment = "List of dimensions you don't want to unload. Uses dimension ID.";
        blacklistDimIDs = propBlacklistDimIDs.getIntList();

        Property propBlacklistDimStrings = config.get(Configuration.CATEGORY_GENERAL, "blacklistDimStrings", new String[]{"Overworld"});
        propBlacklistDimStrings.comment = "List of dimensions you don't want to unload. Uses dimension name.";
        blacklistDimStrings = propBlacklistDimStrings.getStringList();

        Property propUnloadInterval= config.get(Configuration.CATEGORY_GENERAL, "unloadCheck", 600);
        propUnloadInterval.comment = "Time (in ticks) to wait before checking dimensions.";
        unloadInterval = propUnloadInterval.getInt();

        config.save();
    }
}
