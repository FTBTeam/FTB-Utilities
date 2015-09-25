package latmod.ftbu.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface IServerConfig
{
	public String getConfigName();
	public void load();
	public void readConfig(NBTTagCompound tag);
	public void writeConfig(NBTTagCompound tag, EntityPlayerMP ep);
}