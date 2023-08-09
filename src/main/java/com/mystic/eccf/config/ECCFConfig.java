package com.mystic.eccf.config;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;

import java.util.*;
import java.util.function.Supplier;
@Config(modid = "eccf")
public class ECCFConfig {

    public static int maxEntitiesPerChunk = 25;

    public static String entityBlacklistIds = "minecraft:ender_dragon,minecraft:wither,minecraft:snowman";

    // Static initializers go after the properties!
    // This will run automatically when you retrieve any properties from this config class
    static {
        // Load the blacklist from the configuration
        entityBlacklistIds = loadEntityBlacklist().toString();
    }

    // Custom method to deserialize the stored string back to a list
    public static List<String> loadEntityBlacklist() {
        List<String> blacklist = new ArrayList<>();
        String storedBlacklist = entityBlacklistIds;
        String[] ids = storedBlacklist.split(",");
        blacklist.addAll(Arrays.asList(ids));
        return blacklist;
    }
}