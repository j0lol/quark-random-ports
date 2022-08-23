package lol.j0.quark_ports.mixin;

import lol.j0.quark_ports.QuarkPorts;
import lol.j0.quark_ports.management.module.ExpandedItemInteractionsModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ClickType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Inject(at=@At("HEAD"), cancellable = true, method= "onClicked(Lnet/minecraft/item/ItemStack;Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/util/ClickType;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/inventory/StackReference;)Z")
	public void onClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {

		var thisStack = (ItemStack) (Object) this;

		if (!stack.isEmpty() && ExpandedItemInteractionsModule.SHULKER_BOXES.contains(thisStack.getItem())) {
			if (!ExpandedItemInteractionsModule.SHULKER_BOXES.contains(stack.getItem())) {
				ExpandedItemInteractionsModule.hookFromOnClicked(thisStack, slot, clickType, player, cursorStackReference, cir);
			}
		}

		if (!stack.isEmpty() && thisStack.isOf(Items.LAVA_BUCKET)) {
			if (!ExpandedItemInteractionsModule.FIREPROOF_ITEMS.contains(stack.getItem()) && !ExpandedItemInteractionsModule.SHULKER_BOXES.contains(stack.getItem())) {
				ExpandedItemInteractionsModule.tryDestroyItem(thisStack, slot, clickType, player, cursorStackReference, cir);
			}
		}
		if (stack.isEmpty() && thisStack.isOf(Items.GOLDEN_LEGGINGS) && clickType == ClickType.RIGHT) {

		}
	}


	@Inject(at=@At("HEAD"), cancellable = true, method= "onStackClicked(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/util/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Z")
	public void onStackClicked(Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		var thisStack = (ItemStack) (Object) this;
		var stack = slot.getStack();
		if (!stack.isEmpty() && ExpandedItemInteractionsModule.SHULKER_BOXES.contains(thisStack.getItem())) {
			if (!ExpandedItemInteractionsModule.SHULKER_BOXES.contains(stack.getItem())) {
				ExpandedItemInteractionsModule.hookFromOnStackClicked(thisStack, slot, clickType, player, cir);
			}
		}
	}
}


