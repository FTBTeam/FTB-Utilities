package latmod.ftbu.core.net;

import io.netty.buffer.ByteBuf;
import latmod.ftbu.mod.FTBU;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public abstract class MessageLM<E extends MessageLM<?>> implements IMessage, IMessageHandler<E, IMessage>
{
	public static final SimpleNetworkWrapper NET = NetworkRegistry.INSTANCE.newSimpleChannel(FTBU.mod.modID);
	
	public static void init()
	{
		NET.registerMessage(MessageLMWorldUpdate.class, MessageLMWorldUpdate.class, 0, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerUpdate.class, MessageLMPlayerUpdate.class, 1, Side.CLIENT);
		NET.registerMessage(MessageCustomClientAction.class, MessageCustomClientAction.class, 2, Side.SERVER);
		NET.registerMessage(MessageCustomServerAction.class, MessageCustomServerAction.class, 3, Side.CLIENT);
		NET.registerMessage(MessageClientTileAction.class, MessageClientTileAction.class, 4, Side.SERVER);
		NET.registerMessage(MessageLMPlayerDied.class, MessageLMPlayerDied.class, 5, Side.CLIENT);
		NET.registerMessage(MessageReload.class, MessageReload.class, 6, Side.CLIENT);
		NET.registerMessage(MessageNotifyPlayer.class, MessageNotifyPlayer.class, 7, Side.CLIENT);
		NET.registerMessage(MessageManageGroups.class, MessageManageGroups.class, 8, Side.SERVER);
		NET.registerMessage(MessageClientItemAction.class, MessageClientItemAction.class, 9, Side.SERVER);
		NET.registerMessage(MessageLMPlayerLoggedIn.class, MessageLMPlayerLoggedIn.class, 10, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerLoggedOut.class, MessageLMPlayerLoggedOut.class, 11, Side.CLIENT);
		NET.registerMessage(MessageOpenGui.class, MessageOpenGui.class, 12, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerInfo.class, MessageLMPlayerInfo.class, 13, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerRequestInfo.class, MessageLMPlayerRequestInfo.class, 14, Side.SERVER);
		NET.registerMessage(MessageUpdateConfig.class, MessageUpdateConfig.class, 15, Side.CLIENT);
		NET.registerMessage(MessageAreaRequest.class, MessageAreaRequest.class, 16, Side.SERVER);
		NET.registerMessage(MessageAreaUpdate.class, MessageAreaUpdate.class, 17, Side.CLIENT);
		NET.registerMessage(MessageClaimChunk.class, MessageClaimChunk.class, 18, Side.SERVER);
	}
	
	public static void sendTo(EntityPlayerMP ep, MessageLM<?> m)
	{ if(ep == null) NET.sendToAll(m); else NET.sendTo(m, ep); }
	
	// End of static //
	
	public abstract void fromBytes(ByteBuf bb);
	public abstract void toBytes(ByteBuf bb);
	public abstract IMessage onMessage(E m, MessageContext ctx);
	
	public static String readString(ByteBuf bb)
	{
		int i = bb.readShort();
		if(i == -1) return null;
		if(i == 0) return "";
		byte[] b = new byte[i];
		bb.readBytes(b);
		return new String(b);
	}
	
	public static void writeString(ByteBuf bb, String s)
	{
		if(s == null) bb.writeShort(-1);
		else if(s.isEmpty()) bb.writeShort(0);
		else
		{
			byte[] b = s.getBytes();
			bb.writeShort(b.length);
			bb.writeBytes(b);
		}
	}
	
	public static NBTTagCompound readTagCompound(ByteBuf bb)
	{
		int s = bb.readInt();
		if (s >= 0)
		{
			byte[] b = new byte[s]; bb.readBytes(b);
			try { return CompressedStreamTools.func_152457_a(b, NBTSizeTracker.field_152451_a); }
			catch(Exception e) { }
		}
		
		return null;
	}
	
	public static void writeTagCompound(ByteBuf bb, NBTTagCompound tag)
	{
		if (tag == null) bb.writeInt(-1);
		else
		{
			try
			{
				byte[] b = CompressedStreamTools.compress(tag);
				bb.writeInt(b.length);
				bb.writeBytes(b);
			}
			catch(Exception e) { bb.writeInt(-1); }
		}
	}
}