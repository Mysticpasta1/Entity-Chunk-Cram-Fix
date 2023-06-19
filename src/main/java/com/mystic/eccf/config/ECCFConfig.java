package com.mystic.eccf.config;


import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class ECCFConfig {
    public static final Supplier<Integer> maxEntitiesPerChunk;

    static{
        IConfigBuilder builder = ConfigBuilders.newTomlConfig("eccf", null, false);
        builder.push("common");
        maxEntitiesPerChunk = builder.comment("Max Number of Entities Per Chunk").define("maxEntitiesPerChunk", 25, 0, 50);
        builder.pop();
        builder.build();
    }
}