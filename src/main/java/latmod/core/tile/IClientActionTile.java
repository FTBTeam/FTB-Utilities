package latmod.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IClientActionTile
{
	public void onClientAction(EntityPlayer ep, String action, NBTTagCompound data);
}