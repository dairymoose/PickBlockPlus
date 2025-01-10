package com.chaosthedude.pickblockplus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;

public class Util {

	public static boolean areItemStacksIdentical(ItemStack a, ItemStack b) {
		if (a == null && b == null) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		return canItemStacksMerge(a, b);
	}

	public static boolean canItemStacksMerge(ItemStack a, ItemStack b) {
		if (a == null || b == null) {
			return true;
		}

		return a.getDamageValue() == b.getDamageValue() && ItemStack.isSameItemSameTags(a, b);
	}

//	public static ItemStack getBrokenBlock(Level world, BlockPos pos) {
//		Block block = world.getBlockState(pos).getBlock();
//		if (block == null) {
//			return null;
//		}
//
//		List<ItemStack> dropped = block.getDrops(world.getBlockState(pos), world, pos, null);
//		if (dropped == null || dropped.isEmpty()) {
//			return null;
//		}
//
//		ItemStack main = (ItemStack) dropped.remove(0);
//		for (int i = 0; i < dropped.size(); i++) {
//			ItemStack other = (ItemStack) dropped.get(i);
//			if (!canItemStacksMerge(main, other)) {
//				return null;
//			}
//
//			main.grow(other.getCount());
//		}
//
//		return main;
//	}
	
	public static int getMostEffectiveItemSlot(Player player, BlockState state) {
		List<Integer> possibleItems = new ArrayList();
		for (int invSlot = 0; invSlot < player.getInventory().items.size(); invSlot++) {
			ItemStack possibleItem = player.getInventory().items.get(invSlot);
			if (possibleItem != null) {
//				Set<String> toolClasses = possibleItem.getItem().getToolClasses(possibleItem);
//				for (String toolClass : toolClasses) {
//					if (state.getBlock().canHarvestBlock(state, null, null, player).isToolEffective(toolClass, state)) {
//						possibleItems.add(invSlot);
//					} else if (state.getBlock().getHarvestLevel(state) == -1) {
//						if (state.getBlock().getMaterial(state) == Material.ROCK && toolClasses.contains("pickaxe")) {
//							possibleItems.add(invSlot);
//						}
//					}
//				}
				if (possibleItem.isCorrectToolForDrops(state)) {
					possibleItems.add(invSlot);
				}
			}
		}

		int bestSlot = -1;
		for (int invSlot : possibleItems) {
			ItemStack stack = player.getInventory().getItem(invSlot);
			if (stack != null) {
				if (bestSlot == -1) {
					bestSlot = invSlot;
				} else {
					ItemStack bestStack = player.getInventory().getItem(bestSlot);
					//Item possibleTool = stack.getItem();
					//Item bestTool = bestStack.getItem();
					if (stack.getDestroySpeed(state) > bestStack.getDestroySpeed(state)) {
						bestStack = stack;
						bestSlot = invSlot;
					}
				}
			}
		}
		
		return bestSlot;
	}

	public static int getHighestDamageItemSlot(Player player) {
		int highestDamageSlot = -1;
		double highestDamage = -1D;
		double highestSpeed = -1D;
		double highestDps = -1D;
		NonNullList<ItemStack> inventory = player.getInventory().items;
		for (int invSlot = 0; invSlot < inventory.size(); invSlot++) {
			ItemStack stack = inventory.get(invSlot);
			if (stack != null) {
				if (highestDamageSlot == -1) {
					highestDamageSlot = invSlot;
					highestDamage = getDamageForItemStack(stack);
					highestSpeed = getSpeedForItemStack(stack);
					highestDps = highestDamage * highestSpeed;
					//PickBlockPlusClient.LOGGER.info("initialSlot: highestDps is now: " + highestDps + " from item: " + stack + " with dmg=" + highestDamage + " and speed=" + highestSpeed);
				} else {
					double damage = getDamageForItemStack(stack);
					double speed = getSpeedForItemStack(stack);
					double dps = damage * speed;
					
					if (ClientTickHandler.weaponSwapPreferDps) {
						//PickBlockPlusClient.LOGGER.info("check item: " + stack + " with dmg=" + damage + " and speed=" + speed);
						if (dps > highestDps) {
							highestDps = dps;
							highestDamageSlot = invSlot;
							//PickBlockPlusClient.LOGGER.info("highestDps is now: " + highestDps + " from item: " + stack + " with dmg=" + damage + " and speed=" + speed);
						}
					} else {
						if (damage > highestDamage) {
							highestDamage = damage;
							highestDamageSlot = invSlot;
						} else if (damage == highestDamage) {
							if (speed > highestSpeed) {
								highestSpeed = speed;
								highestDamageSlot = invSlot;
							}
						}
					}
				}
			}
		}

		return highestDamageSlot;
	}

	private static double computeTotalForAttributeModifier(Player player, ItemStack itemStack, AttributeModifier attributemodifier) {
		double d0 = attributemodifier.getAmount();
        boolean flag = false;
        if (player != null) {
           if (isWeaponDamageModifier(attributemodifier)) {
              d0 += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
              d0 += (double)EnchantmentHelper.getDamageBonus(itemStack, MobType.UNDEFINED);
           } else if (isAttackSpeedModifier(attributemodifier)) {
              d0 += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
           }
        }
        return d0;
	}
	
	private static double getSpeedForItemStack(ItemStack stack) {
		double speed = 0;
		Multimap<Attribute, AttributeModifier> map = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
		Collection<AttributeModifier> collection1 = map.get(Attributes.ATTACK_SPEED);
		for (AttributeModifier o : collection1) {
			AttributeModifier modifier = (AttributeModifier) o;
			if (isAttackSpeedModifier(modifier)) {
				speed = computeTotalForAttributeModifier(Minecraft.getInstance().player, stack, modifier);
				//speed = modifier.getAmount();
			}
		}
		return speed;
	}

	private static boolean isAttackSpeedModifier(AttributeModifier attributemodifier) {
		return attributemodifier.getId() == Item.BASE_ATTACK_SPEED_UUID;
	}

	private static double getDamageForItemStack(ItemStack stack) {
		double damage = 0;
		Multimap<Attribute, AttributeModifier> map = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
		Collection<AttributeModifier> collection = map.get(Attributes.ATTACK_DAMAGE);
		for (AttributeModifier o : collection) {
			AttributeModifier modifier = (AttributeModifier) o;
			if (isWeaponDamageModifier(modifier)) {
				damage = computeTotalForAttributeModifier(Minecraft.getInstance().player, stack, modifier);
				//damage = modifier.getAmount();
			}
		}
		return damage;
	}

	private static boolean isWeaponDamageModifier(AttributeModifier attributemodifier) {
		return attributemodifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID;
	}

	public static void swapItems(Player player, ItemStack held, int invSlot, ItemStack[] hotbar, boolean isWeaponSwap, boolean isToolSwap) {
		int targetSlot = player.getInventory().selected;
		
		if (isWeaponSwap && ClientTickHandler.dedicatedWeaponSlot != 0) {
			targetSlot = ClientTickHandler.dedicatedWeaponSlot - 1;
			player.getInventory().selected = targetSlot;
		} else if (isToolSwap && ClientTickHandler.dedicatedToolSlot != 0) {
			targetSlot = ClientTickHandler.dedicatedToolSlot - 1;
			player.getInventory().selected = targetSlot;
		}
		
		Minecraft.getInstance().gameMode.handleInventoryMouseClick(player.inventoryMenu.containerId, invSlot, targetSlot, ClickType.SWAP, player);
		
		if (held == null) {
			return;
		}

		if (hotbar[targetSlot] == null) {
			hotbar[targetSlot] = held;
			return;
		}

		boolean canReplace = false;
		for (int barSlot = 0; barSlot < 9; barSlot++) {
			ItemStack barItem = player.getInventory().getItem(barSlot);
			if (barItem != null && barItem == hotbar[targetSlot]) {
				canReplace = true;
				break;
			}
		}

		if (canReplace) {
			hotbar[targetSlot] = held;
		}
	}

}
