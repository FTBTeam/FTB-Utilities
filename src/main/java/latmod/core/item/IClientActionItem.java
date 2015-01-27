package latmod.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IClientActionItem extends IItemLM
{
	public ItemStack onClientAction(ItemStack is, EntityPlayer ep, String action, NBTTagCompound data);
}