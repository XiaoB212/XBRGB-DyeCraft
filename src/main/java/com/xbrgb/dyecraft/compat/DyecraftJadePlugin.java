/*
 * Copyright [2026] [XiaoB212 of copyright owner]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xbrgb.dyecraft.compat;

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.block.DyeableBlock;
import net.minecraft.util.Identifier;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class DyecraftJadePlugin implements IWailaPlugin {
    public static final Identifier DYEABLE_BLOCK = Identifier.of(DyecraftMod.MOD_ID, "dyeable_block");

    @Override
    public void register(IWailaCommonRegistration registration) {
        DyecraftMod.LOGGER.info("Jade plugin registered (common)");
        registration.registerBlockDataProvider(DyeableBlockComponentProvider.INSTANCE, DyeableBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        DyecraftMod.LOGGER.info("Jade plugin registered (client)");
        registration.registerBlockComponent(DyeableBlockComponentProvider.INSTANCE, DyeableBlock.class);
    }
}