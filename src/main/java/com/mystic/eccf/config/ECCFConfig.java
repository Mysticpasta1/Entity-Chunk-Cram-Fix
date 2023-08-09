package com.mystic.eccf.config;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;
import java.util.function.Supplier;
@Config(modid = "eccf")
public class ECCFConfig {

    public static int maxEntitiesPerChunk = 25;

    // Static initializers go after the properties!
    // This will run automatically when you retrieve any properties from this config class
    static {
        ConfigAnytime.register(ECCFConfig.class);
    }

}