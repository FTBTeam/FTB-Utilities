package latmod.ftbu.core.api;

import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;

public interface ICustomActionFromServer
{
	public String getActionHandlerID();
	public NBTTagCompound sendToClient(EntityPlayerMP ep);
	public void readFromServer(EntityPlayer ep, NBTTagCompound data);
}