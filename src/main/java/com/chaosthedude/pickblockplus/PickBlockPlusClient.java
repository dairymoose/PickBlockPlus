package com.chaosthedude.pickblockplus;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class PickBlockPlusClient {

	public static final String MODID = "pickblockplus";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final KeyMapping PICK_BLOCK_HOTKEY = new KeyMapping("key.pickblockplus", InputConstants.Type.KEYSYM, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, "key.categories.gameplay");
	
    public static final List<KeyMapping> allKeyMappings = List.of(PICK_BLOCK_HOTKEY);
	
	public PickBlockPlusClient() {
		LOGGER.debug("PickBlockPlusClient");
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

		MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PickBlockPlusConfig.SPEC);
	}
	
	private void clientSetup(final FMLClientSetupEvent event) {
		;
	}
	
	@Mod.EventBusSubscriber(modid = PickBlockPlus.MODID, bus = Bus.MOD, value = Dist.CLIENT)
    public class OptiscaleClientModEventBusHandler {
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
			LOGGER.info("register keybinds");
			for (KeyMapping mapping : allKeyMappings) {
				event.register(mapping);
			}
        }
    }

}
