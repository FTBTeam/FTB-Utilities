package latmod.core.net;

import io.netty.buffer.ByteBuf;
import latmod.core.mod.LC;
import net.minecraft.nbt.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public abstract class MessageLM<E extends MessageLM<?>> implements IMessage, IMessageHandler<E, IMessage>
{
	public static final SimpleNetworkWrapper NET = NetworkRegistry.INSTANCE.newSimpleChannel(LC.mod.modID);
	
	public static void init()
	{
		NET.registerMessage(MessageUpdateAllData.class, MessageUpdateAllData.class, 0, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerUpdate.class, MessageLMPlayerUpdate.class, 1, Side.CLIENT);
		NET.registerMessage(MessageCustomClientAction.class, MessageCustomClientAction.class, 2, Side.SERVER);
		NET.registerMessage(MessageCustomServerAction.class, MessageCustomServerAction.class, 3, Side.CLIENT);
		NET.registerMessage(MessageClientTileAction.class, MessageClientTileAction.class, 4, Side.SERVER);
		//
		NET.registerMessage(MessageReload.class, MessageReload.class, 6, Side.CLIENT);
		NET.registerMessage(MessageNotifyPlayer.class, MessageNotifyPlayer.class, 7, Side.CLIENT);
		NET.registerMessage(MessageManageGroups.class, MessageManageGroups.class, 8, Side.SERVER);
		NET.registerMessage(MessageClientItemAction.class, MessageClientItemAction.class, 9, Side.SERVER);
		NET.registerMessage(MessageLMPlayerLoggedIn.class, MessageLMPlayerLoggedIn.class, 10, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerLoggedOut.class, MessageLMPlayerLoggedOut.class, 11, Side.CLIENT);
		NET.registerMessage(MessageCustomClientGUI.class, MessageCustomClientGUI.class, 12, Side.CLIENT);
	}
	
	// End of static //
	
	public NBTTagCompound data = null;
	
	public final void fromBytes(ByteBuf bb)
	{
		data = null;
		
		int s = bb.readInt();
		if (s >= 0)
		{
			byte[] b = new byte[s]; bb.readBytes(b);
			try { data = CompressedStreamTools.func_152457_a(b, new NBTSizeTracker(2097152L)); }
			catch(Exception e) { }
		}
	}
	
	public final void toBytes(ByteBuf bb)
	{
		if (data == null) bb.writeInt(-1);
		else
		{
			try
			{
				byte[] b = CompressedStreamTools.compress(data);
				bb.writeInt(b.length);
				bb.writeBytes(b);
			}
			catch(Exception e) { bb.writeInt(-1); }
		}
	}
	
	public final IMessage onMessage(E m, MessageContext ctx)
	{
		//LatCoreMC.logger.info(ctx.side + " :: " + getClass().getSimpleName() + " :: " + m.data);
		data = m.data;
		onMessage(ctx);
		return null;
	}
	
	public abstract void onMessage(MessageContext ctx);
}