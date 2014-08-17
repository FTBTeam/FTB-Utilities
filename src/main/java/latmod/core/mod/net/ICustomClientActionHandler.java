package latmod.core.mod.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface ICustomClientActionHandler
{
	public void onClientAction(EntityPlayer ep, String channel, String action, NBTTagCompound extraData);
}
