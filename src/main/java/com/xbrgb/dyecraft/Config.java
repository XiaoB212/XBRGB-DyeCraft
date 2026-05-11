package com.xbrgb.dyecraft;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // 示例配置项（可自行增删）
    // public static final ModConfigSpec.BooleanValue ENABLE_SOMETHING = BUILDER
    //         .comment("Enable something")
    //         .define("enableSomething", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}