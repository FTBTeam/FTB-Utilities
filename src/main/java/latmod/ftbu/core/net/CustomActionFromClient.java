package latmod.ftbu.core.net;

import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;

public interface CustomActionFromClient
{
	public void sendToServer(EntityPlayer ep, NBTTagCompound data);
	public void readFromClient(EntityPlayerMP ep, NBTTagCompound data);
}