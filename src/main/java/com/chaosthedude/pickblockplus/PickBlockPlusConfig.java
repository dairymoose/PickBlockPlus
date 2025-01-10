package com.chaosthedude.pickblockplus;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = PickBlockPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PickBlockPlusConfig
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final String LOG_PREFIX = "[CobbleSpawnTracker]: ";

    private static final ForgeConfigSpec.BooleanValue REVERSE_CROUCH_LOGIC = BUILDER
            .comment("Reverse crouching logic")
            .define("reverse_crouch_logic", false);
    
    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void reinit() {
		try {
	    	LOGGER.debug(PickBlockPlusConfig.LOG_PREFIX + "Load Configuration");
	    	
	    	ClientTickHandler.reversePickingLogic = REVERSE_CROUCH_LOGIC.get().booleanValue();
	    	
		} catch (Exception ex) {
			LOGGER.error("Error initializing config", ex);
		}
	}
	
	@SubscribeEvent
	public static void onConfigReloaded(ModConfigEvent.Reloading event) {
		if (SPEC.isLoaded()) {
			PickBlockPlusConfig.reinit();
		}
	}
    
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
    	reinit();
    }
}
