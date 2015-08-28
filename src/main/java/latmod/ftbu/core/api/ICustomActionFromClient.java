package latmod.ftbu.core.api;

import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;

public interface ICustomActionFromClient
{
	public String getActionHandlerID();
	public NBTTagCompound sendToServer(EntityPlayer ep);
	public void readFromClient(EntityPlayerMP ep, NBTTagCompound data);
}