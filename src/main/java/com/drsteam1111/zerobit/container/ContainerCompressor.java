package com.drsteam1111.zerobit.container;

import com.drsteam1111.zerobit.container.slot.SlotCompressorFuel;
import com.drsteam1111.zerobit.container.slot.SlotCompressorOutput;
import com.drsteam1111.zerobit.container.slot.SlotIngots;
import com.drsteam1111.zerobit.init.ModItems;
import com.drsteam1111.zerobit.tileentity.TileEntityCompressor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by edvin on 2017-06-03.
 */
public class ContainerCompressor extends Container {

    private TileEntityCompressor te;
    private IItemHandler handler;

    public ContainerCompressor(IInventory playerInv, TileEntityCompressor te) {
        this.te = te;
        this.handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        //Our tile entity slots
        this.addSlotToContainer(new SlotIngots(handler, 0, 80, 17));
        this.addSlotToContainer(new SlotIngots(handler, 1, 80, 35));
        this.addSlotToContainer(new SlotIngots(handler, 2, 80, 53));
        this.addSlotToContainer(new SlotCompressorOutput(handler, 3, 116, 35));
        this.addSlotToContainer(new SlotCompressorFuel(handler, 4, 44, 35));

        //The player's inventory slots
        int xPos = 8; //The x position of the top left player inventory slot on our texture
        int yPos = 84; //The y position of the top left player inventory slot on our texture

        //Player slots
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, xPos + x * 18, yPos + y * 18));
            }
        }

        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(playerInv, x, xPos + x * 18, yPos + 58));
        }
    }

    /**
     * Checks that the player can put items in and out of the container
     */
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.te.isUseableByPlayer(player);
    }

    /**
     * Called when the player presses shift and takes an item out of the container
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
        ItemStack previous = ItemStack.EMPTY;
        Slot slot = (Slot) this.inventorySlots.get(fromSlot);

        if (slot != null && slot.getHasStack()) {
            ItemStack current = slot.getStack();
            previous = current.copy();

            if (fromSlot < this.handler.getSlots()) {
                // From the compressor inventory to player's inventory
                if (!this.mergeItemStack(current, handler.getSlots(), handler.getSlots() + 36, true))
                    return ItemStack.EMPTY;
            } else {
                // From the player's inventory to compressor's inventory
                if(current.getItem() == ModItems.ingot) {
                    if(!this.mergeItemStack(current, 9, handler.getSlots(), false))
                        return ItemStack.EMPTY;
                }
                if (!this.mergeItemStack(current, 0, handler.getSlots(), false))
                    return ItemStack.EMPTY;
            }

            if (current.getCount() == 0) //Use func_190916_E() instead of stackSize 1.11 only 1.11.2 use getCount()
                slot.putStack(ItemStack.EMPTY); //Use ItemStack.field_190927_a instead of (ItemStack)null for a blank item stack. In 1.11.2 use ItemStack.EMPTY
            else
                slot.onSlotChanged();

            if (current.getCount() == previous.getCount())
                return null;
            slot.onTake(playerIn, current);
        }
        return previous;
    }

}