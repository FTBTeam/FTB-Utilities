package latmod.ftbu.core.net;

import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.world.*;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageAreaRequest extends MessageLM<MessageAreaRequest>
{
	public int chunkX, chunkZ, dim, size, playerID;
	
	public MessageAreaRequest() { }
	
	@SideOnly(Side.CLIENT)
	public MessageAreaRequest(int x, int z, int d, int s)
	{
		chunkX = x;
		chunkZ = z;
		dim = d;
		size = s;
		playerID = LMWorldClient.inst.clientPlayerID;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		chunkX = bb.readInt();
		chunkZ = bb.readInt();
		dim = bb.readInt();
		size = bb.readByte();
		playerID = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(chunkX);
		bb.writeInt(chunkZ);
		bb.writeInt(dim);
		bb.writeByte(size);
		bb.writeInt(playerID);
	}
	
	public IMessage onMessage(MessageAreaRequest m, MessageContext ctx)
	{ return new MessageAreaUpdate(m.chunkX, m.chunkZ, m.dim, m.size, LMWorldServer.inst.getPlayer(m.playerID)); }
}