package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.world.LMWorldServer;
import io.netty.buffer.ByteBuf;
import latmod.lib.MathHelperLM;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageAreaRequest extends MessageLM<MessageAreaRequest>
{
	public int chunkX, chunkZ, sizeX, sizeZ;
	
	public MessageAreaRequest() { }
	
	public MessageAreaRequest(int x, int y, int w, int h)
	{
		chunkX = x;
		chunkZ = y;
		sizeX = MathHelperLM.clampInt(w, 1, 255);
		sizeZ = MathHelperLM.clampInt(h, 1, 255);
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		chunkX = io.readInt();
		chunkZ = io.readInt();
		sizeX = io.readInt();
		sizeZ = io.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeInt(chunkX);
		io.writeInt(chunkZ);
		io.writeInt(sizeX);
		io.writeInt(sizeZ);
	}
	
	@Override
	public IMessage onMessage(MessageAreaRequest m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		return new MessageAreaUpdate(LMWorldServer.inst.getPlayer(ep), m.chunkX, m.chunkZ, ep.dimension, m.sizeX, m.sizeZ);
	}
}