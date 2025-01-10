package com.chaosthedude.pickblockplus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientTickHandler {

	private final Minecraft mc = Minecraft.getInstance();
	private ItemStack[] hotbar = new ItemStack[9];
	private boolean activated = true;
	private int ticksSincePressed = 0;
	public static boolean reversePickingLogic = true;

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START || event.type != TickEvent.Type.CLIENT) {
			return;
		}

		//overrideVanillaPickBlock();
		checkPickBlockKey();
	}

	private void checkPickBlockKey() {
		Player player = mc.player;
		if (player == null) {
			return;
		}

		if (!PickBlockPlusClient.PICK_BLOCK_HOTKEY.isDown()) {
			activated = false;
			ticksSincePressed++;
			if (ticksSincePressed > 20) {
				ticksSincePressed = 20;
			}

			return;
		}

		if (activated) {
			return;
		}

		activated = true;
		if (mc.screen != null) {
			return;
		}

		HitResult target = mc.hitResult;
		if (target == null) {
			return;
		}

		if (player.isCreative()) {
			//ForgeHooks.onPickBlock(target, player, mc.level);
			mc.pickBlock();
			mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return;
		}

		int slot = player.getInventory().selected;
		if (hotbar.length != 9) {
			hotbar = new ItemStack[9];
		}

		boolean isStanding = !player.isCrouching();
		
		if (reversePickingLogic)
			isStanding = !isStanding;
			
		if (isStanding) {
			ticksSincePressed = 0;
			mc.pickBlock();
			//mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		} else {
			ticksSincePressed = 0;
			boolean targetIsEntity = false;
			BlockState state = null;
			if (target.getType() == HitResult.Type.BLOCK) {
				state = player.level().getBlockState(((BlockHitResult)target).getBlockPos());
			} else if (target.getType() == HitResult.Type.ENTITY) {
				targetIsEntity = true;
			}

			if (!targetIsEntity && state == null) {
				return;
			}

			ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
			int bestSlot = -1;
			if (targetIsEntity) {
				bestSlot = Util.getHighestDamageItemSlot(player);
			} else {
				bestSlot = Util.getMostEffectiveItemSlot(player, state);
			}
			
			if (bestSlot == -1) {
				return;
			}

			//mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			if (bestSlot < 9) {
				player.getInventory().selected = bestSlot;
				return;
			}

			Util.swapItems(player, held, bestSlot, hotbar);
			return;
		}
	}

//	private void overrideVanillaPickBlock() {
//		GameSettings settings = Minecraft.getMinecraft().gameSettings;
//		if (settings.keyBindPickBlock.getKeyCode() != 0 && PickBlockPlusClient.pickBlockPlus.getKeyCode() == 0) {
//			PickBlockPlusClient.logger.info("Overriding vanilla pick block");
//			settings.setOptionKeyBinding(PickBlockPlusClient.pickBlockPlus, settings.keyBindPickBlock.getKeyCode());
//			settings.setOptionKeyBinding(settings.keyBindPickBlock, 0);
//			KeyBinding.resetKeyBindingArrayAndHash();
//		}
//	}

}
