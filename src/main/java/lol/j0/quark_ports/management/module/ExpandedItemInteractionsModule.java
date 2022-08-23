package lol.j0.quark_ports.management.module;

import lol.j0.quark_ports.QuarkPorts;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;

public class ExpandedItemInteractionsModule {

	public static final ArrayList<Item> SHULKER_BOXES = new ArrayList<>(Arrays.asList(
			Items.SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.BLACK_SHULKER_BOX, Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX,
			Items.YELLOW_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX,
			Items.PURPLE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.LIME_SHULKER_BOX,
			Items.LIGHT_BLUE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.WHITE_SHULKER_BOX)
	);
	public static final ArrayList<Item> FIREPROOF_ITEMS = new ArrayList<>(Arrays.asList(
			Items.NETHERITE_AXE, Items.NETHERITE_BLOCK, Items.NETHERITE_BOOTS, Items.NETHERITE_HOE,
			Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_INGOT, Items.NETHERITE_LEGGINGS,
			Items.NETHERITE_PICKAXE, Items.NETHERITE_SCRAP, Items.NETHERITE_SHOVEL, Items.NETHERITE_SWORD,
			Items.ANCIENT_DEBRIS, Items.LODESTONE)
	);

	// can you call kotlin from mixins? unsure

	public static void hookFromOnClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
		addToShulker(stack, cursorStackReference.get(), slot, clickType, player, cursorStackReference, cir, false);

	}
	public static void hookFromOnStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		addToShulker(stack, slot.getStack(), slot, clickType, player, StackReference.EMPTY, cir, true);
	}
	public static void addToShulker(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir, boolean stackClicked) {
		var items = stack.getOrCreateNbt().getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE);

		ItemStack[] inventorySlots = new ItemStack[27];

		for (NbtElement x : items) {
			var item = (NbtCompound) x;
			QuarkPorts.LOGGER.info(String.valueOf(item));
			var itemSlot = item.getByte("Slot");
			QuarkPorts.LOGGER.info(String.valueOf(itemSlot));
			if ( itemSlot < 27 && itemSlot >= 0 ) {
				inventorySlots[item.getByte("Slot")] = ItemStack.fromNbt(item);
			}
		}
		ItemStack[] invStacks;
		if (stackClicked) {
			invStacks = addItemToInv(inventorySlots, otherStack, slot);
		} else {
			invStacks = addItemToInv(inventorySlots, otherStack, cursorStackReference);
		}
		NbtList nbtList = new NbtList();
		for (int i = 0; i < invStacks.length; i++) {
			ItemStack invStack = invStacks[i];
			if (invStack != null) {
				NbtCompound nbtCompound = new NbtCompound();
				invStack.writeNbt(nbtCompound);
				nbtCompound.putByte("Slot", (byte) (i));
				nbtList.add(nbtCompound);
			}
		}
		stack.getOrCreateNbt().getCompound("BlockEntityTag").put("Items", nbtList);

		cir.setReturnValue(true);
		cir.cancel();
	}

	private static ItemStack[] addItemToInv(ItemStack[] inventoryStacks, ItemStack itemStack, StackReference cursorStackReference) {

		for (int i = 0; i < inventoryStacks.length; i++) {

			QuarkPorts.LOGGER.info(String.valueOf(i));
			QuarkPorts.LOGGER.info(Arrays.toString(inventoryStacks));

			ItemStack inventoryStack = inventoryStacks[i];

			if (inventoryStack == null) {
				inventoryStacks[i] = itemStack;
				cursorStackReference.set(ItemStack.EMPTY);
				return inventoryStacks;
			} else if (inventoryStack.isStackable() && ( inventoryStack.getCount() < inventoryStack.getMaxCount() ) && inventoryStack.isItemEqual(itemStack)) {
				if (inventoryStack.getCount() + itemStack.getCount() <= inventoryStack.getMaxCount()) {
					inventoryStack.increment(itemStack.getCount());
					cursorStackReference.set(ItemStack.EMPTY);
					return inventoryStacks;
				} else {
					itemStack.decrement(inventoryStack.getMaxCount() - inventoryStack.getCount());
					inventoryStack.setCount(inventoryStack.getMaxCount());
				}
			}
		}
		return inventoryStacks;
	}

	private static ItemStack[] addItemToInv(ItemStack[] inventoryStacks, ItemStack itemStack, Slot slot) {

		for (int i = 0; i < inventoryStacks.length; i++) {

			QuarkPorts.LOGGER.info(String.valueOf(i));
			QuarkPorts.LOGGER.info(Arrays.toString(inventoryStacks));

			ItemStack inventoryStack = inventoryStacks[i];

			if (inventoryStack == null) {
				inventoryStacks[i] = itemStack;
				slot.setStack(ItemStack.EMPTY);
				return inventoryStacks;
			} else if (inventoryStack.isStackable() && ( inventoryStack.getCount() < inventoryStack.getMaxCount() ) && inventoryStack.isItemEqual(itemStack)) {
				if (inventoryStack.getCount() + itemStack.getCount() <= inventoryStack.getMaxCount()) {
					inventoryStack.increment(itemStack.getCount());
					slot.setStack(ItemStack.EMPTY);
					return inventoryStacks;
				} else {
					itemStack.decrement(inventoryStack.getMaxCount() - inventoryStack.getCount());
					inventoryStack.setCount(inventoryStack.getMaxCount());
				}
			}
		}
		return inventoryStacks;
	}

	public static void tryDestroyItem(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
		if (clickType == ClickType.RIGHT) {
			player.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH);
			cursorStackReference.set(ItemStack.EMPTY);
			cir.setReturnValue(true);
			cir.cancel();
		}

	}
}
