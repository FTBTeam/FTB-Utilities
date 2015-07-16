package latmod.ftbu.core.net;

import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;

public interface CustomActionFromServer
{
	public void sendToClient(EntityPlayerMP ep, NBTTagCompound data);
	public void readFromServer(EntityPlayer ep, NBTTagCompound data);
}