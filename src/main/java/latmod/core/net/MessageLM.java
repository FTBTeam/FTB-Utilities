package latmod.core.net;

import io.netty.buffer.ByteBuf;
import latmod.core.mod.LC;
import net.minecraft.nbt.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageLM implements IMessage
{
	public static final SimpleNetworkWrapper NET = NetworkRegistry.INSTANCE.newSimpleChannel(LC.mod.modID);
	
	public static void init()
	{
		NET.registerMessage(MessageUpdateLMData.class, MessageUpdateLMData.class, 0, Side.CLIENT);
		NET.registerMessage(MessageUpdateLMPlayer.class, MessageUpdateLMPlayer.class, 1, Side.CLIENT);
		NET.registerMessage(MessageCustomClientAction.class, MessageCustomClientAction.class, 2, Side.SERVER);
		NET.registerMessage(MessageCustomServerAction.class, MessageCustomServerAction.class, 3, Side.CLIENT);
		NET.registerMessage(MessageClientTileAction.class, MessageClientTileAction.class, 4, Side.SERVER);
		NET.registerMessage(MessageLMKeyPressed.class, MessageLMKeyPressed.class, 5, Side.SERVER);
		NET.registerMessage(MessageReload.class, MessageReload.class, 6, Side.CLIENT);
		NET.registerMessage(MessageNotifyPlayer.class, MessageNotifyPlayer.class, 7, Side.CLIENT);
		NET.registerMessage(MessageManageGroups.class, MessageManageGroups.class, 8, Side.SERVER);
		NET.registerMessage(MessageClientItemAction.class, MessageClientItemAction.class, 9, Side.SERVER);
	}
	
	// End of static //
	
	public NBTTagCompound data = null;
	
	public final void fromBytes(ByteBuf bb)
	{
		data = null;
		
		short s = bb.readShort();
		if (s >= 0)
		{
			byte[] b = new byte[s]; bb.readBytes(b);
			try { data = CompressedStreamTools.func_152457_a(b, new NBTSizeTracker(2097152L)); }
			catch(Exception e) { }
		}
	}
	
	public final void toBytes(ByteBuf bb)
	{
		if (data == null) bb.writeShort(-1);
		else
		{
			try
			{
				byte[] b = CompressedStreamTools.compress(data);
				bb.writeShort((short)b.length);
				bb.writeBytes(b);
			}
			catch(Exception e) { bb.writeByte(-1); }
		}
	}
}