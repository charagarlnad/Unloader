package unloader;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class UnloaderConfig {
    private final Configuration config;

    public String[] blacklistDims;
    public int unloadInterval;

    public UnloaderConfig(File file) {
        config = new Configuration(file);

        Property propBlacklistDims = config.get(Configuration.CATEGORY_GENERAL, "blacklistDims", new String[]{"0", "overworld"});
        propBlacklistDims.comment = "List of dimensions you don’t want to unload. Can be dimension name or ID. Uses regular expressions.";
        blacklistDims = propBlacklistDims.getStringList();

        Property propUnloadInterval= config.get(Configuration.CATEGORY_GENERAL, "unloadCheck", 600);
        propUnloadInterval.comment = "Time (in ticks) to wait before checking dimensions.";
        unloadInterval = propUnloadInterval.getInt();

    }
}
