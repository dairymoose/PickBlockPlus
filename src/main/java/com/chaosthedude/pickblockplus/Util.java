package com.chaosthedude.pickblockplus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
		for (int invSlot = 0; invSlot < player.getInventory().items.size(); invSlot++) {
			ItemStack stack = player.getInventory().items.get(invSlot);
			if (stack != null) {
				if (highestDamageSlot == -1) {
					highestDamageSlot = invSlot;
				} else {
					double damage = -1D;
					Multimap<Attribute, AttributeModifier> map = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
					Collection<AttributeModifier> collection = map.get(Attributes.ATTACK_DAMAGE);
					for (AttributeModifier o : collection) {
						AttributeModifier modifier = (AttributeModifier) o;
						if (modifier.getName().equals("Weapon modifier") || modifier.getName().equals("generic.attackDamage") || modifier.getName().equals("Tool modifier")) {
							damage = modifier.getAmount();
						}
					}

					if (damage > highestDamage) {
						highestDamage = damage;
						highestDamageSlot = invSlot;
					} else if (damage == highestDamage) {
						double speed = -1D;
						Multimap map1 = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
						Collection<AttributeModifier> collection1 = map1.get(Attributes.ATTACK_SPEED);
						for (AttributeModifier o : collection1) {
							AttributeModifier modifier = (AttributeModifier) o;
							if (modifier.getName().equals("Weapon modifier") || modifier.getName().equals("Tool modifier")) {
								speed = modifier.getAmount();
							}
						}
						
						if (speed < highestSpeed) {
							highestSpeed = speed;
							highestDamageSlot = invSlot;
						}
					}
				}
			}
		}

		return highestDamageSlot;
	}

	public static void swapItems(Player player, ItemStack held, int invSlot, ItemStack[] hotbar) {
		int targetSlot = player.getInventory().selected;
		//Minecraft.getInstance().playerController.pickItem(invSlot);
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
