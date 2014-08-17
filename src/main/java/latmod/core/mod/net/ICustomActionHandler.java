package latmod.core.mod.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;

public interface ICustomActionHandler
{
	public void onAction(EntityPlayer ep, String channel, String action, NBTTagCompound extraData, Side s);
}
