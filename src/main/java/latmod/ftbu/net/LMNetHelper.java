package latmod.ftbu.net;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.mod.FTBU;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import net.minecraft.util.IChatComponent;

public class LMNetHelper
{
	public static final SimpleNetworkWrapper NET = newChannel(FTBU.mod.modID);
	
	public static void init()
	{
		int ID = 0;
		NET.registerMessage(MessageLMWorldUpdate.class, MessageLMWorldUpdate.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerUpdate.class, MessageLMPlayerUpdate.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageClientAction.class, MessageClientAction.class, ++ID, Side.SERVER);
		NET.registerMessage(MessageOpenGui.class, MessageOpenGui.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageReload.class, MessageReload.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageClientTileAction.class, MessageClientTileAction.class, ++ID, Side.SERVER);
		NET.registerMessage(MessageClientItemAction.class, MessageClientItemAction.class, ++ID, Side.SERVER);
		NET.registerMessage(MessageLMPlayerDied.class, MessageLMPlayerDied.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageNotifyPlayer.class, MessageNotifyPlayer.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerLoggedIn.class, MessageLMPlayerLoggedIn.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerLoggedOut.class, MessageLMPlayerLoggedOut.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerInfo.class, MessageLMPlayerInfo.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageUpdateConfig.class, MessageUpdateConfig.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageAreaUpdate.class, MessageAreaUpdate.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageClaimChunk.class, MessageClaimChunk.class, ++ID, Side.SERVER);
	}
	
	public static SimpleNetworkWrapper newChannel(String s)
	{ return NetworkRegistry.INSTANCE.newSimpleChannel(s); }
	
	public static void sendTo(EntityPlayerMP ep, MessageLM<?> m)
	{ if(ep == null) NET.sendToAll(m); else NET.sendTo(m, ep); }
	
	public static void sendToServer(MessageLM<?> m)
	{ NET.sendToServer(m); }
	
	public static NBTTagCompound readTagCompound(ByteIOStream io) throws Exception
	{
		int s = io.readInt();
		if (s >= 0)
		{
			byte[] b = new byte[s];
			io.readRawBytes(b);
			try { return CompressedStreamTools.func_152457_a(b, NBTSizeTracker.field_152451_a); }
			catch(Exception e) { }
		}
		
		return null;
	}
	
	public static void writeTagCompound(ByteIOStream io, NBTTagCompound tag) throws Exception
	{
		if (tag == null) io.writeInt(-1);
		else
		{
			try
			{
				byte[] b = CompressedStreamTools.compress(tag);
				io.writeInt(b.length);
				io.writeRawBytes(b);
			}
			catch(Exception e) { io.writeInt(-1); }
		}
	}
	
	public static IChatComponent readChatComponent(ByteIOStream io) throws Exception
	{ return IChatComponent.Serializer.func_150699_a(io.readUTF()); }
	
	public static void writeChatComponent(ByteIOStream io, IChatComponent c) throws Exception
	{ io.writeUTF(IChatComponent.Serializer.func_150696_a(c)); }
}