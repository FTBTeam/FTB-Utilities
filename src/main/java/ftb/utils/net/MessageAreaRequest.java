package ftb.utils.net;

import ftb.lib.api.net.*;
import ftb.utils.world.LMWorldServer;
import io.netty.buffer.ByteBuf;
import latmod.lib.MathHelperLM;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageAreaRequest extends MessageLM<MessageAreaRequest>
{
	public int chunkX, chunkY, sizeX, sizeY;
	
	public MessageAreaRequest() { }
	
	public MessageAreaRequest(int x, int y, int w, int h)
	{
		chunkX = x;
		chunkY = y;
		sizeX = MathHelperLM.clampInt(w, 1, 255);
		sizeY = MathHelperLM.clampInt(h, 1, 255);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public void fromBytes(ByteBuf io)
	{
		chunkX = io.readInt();
		chunkY = io.readInt();
		sizeX = io.readInt();
		sizeY = io.readInt();
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(chunkX);
		io.writeInt(chunkY);
		io.writeInt(sizeX);
		io.writeInt(sizeY);
	}
	
	public IMessage onMessage(MessageAreaRequest m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		return new MessageAreaUpdate(LMWorldServer.inst.getPlayer(ep), m.chunkX, m.chunkY, ep.dimension, m.sizeX, m.sizeY);
	}
}