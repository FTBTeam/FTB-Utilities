package latmod.core.event;

import latmod.core.util.FastMap;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;

public class CustomAction
{
	public static final FastMap<String, FromClient> cHandlers = new FastMap<String, FromClient>();
	public static final FastMap<String, FromServer> sHandlers = new FastMap<String, FromServer>();
	
	public static void register(String s, FromClient i)
	{ if(!cHandlers.keys.contains(s)) cHandlers.put(s, i); }
	
	public static void register(String s, FromServer i)
	{ if(!sHandlers.keys.contains(s)) sHandlers.put(s, i); }
	
	public static interface FromClient
	{
		public void sendToServer(EntityPlayer ep, NBTTagCompound data);
		public void readFromClient(EntityPlayerMP ep, NBTTagCompound data);
	}
	
	public static interface FromServer
	{
		public void sendToClient(EntityPlayerMP ep, NBTTagCompound data);
		public void readFromServer(EntityPlayer ep, NBTTagCompound data);
	}
}