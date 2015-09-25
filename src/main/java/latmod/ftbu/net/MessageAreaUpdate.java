package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.minimap.Minimap;
import latmod.ftbu.world.*;

public class MessageAreaUpdate extends MessageLM<MessageAreaUpdate> implements IClientMessageLM<MessageAreaUpdate> //MessageAreaRequest
{
	public int chunkX, chunkZ, dim, size;
	public int[] types;
	
	public MessageAreaUpdate() { }
	
	public MessageAreaUpdate(int x, int z, int d, int s, LMPlayerServer p)
	{
		chunkX = x; chunkZ = z; dim = d; size = s; types = new int[s * s];
		for(int z1 = 0; z1 < s; z1++) for(int x1 = 0; x1 < s; x1++)
			types[x1 + z1 * s] = ChunkType.getChunkTypeI(dim, x + x1, z + z1, p);
	}
	
	public MessageAreaUpdate(int x, int z, int d, int type)
	{
		chunkX = x;
		chunkZ = z;
		dim = d;
		size = 1;
		types = new int[] { type };
	}
	
	public void fromBytes(ByteBuf bb)
	{
		chunkX = bb.readInt();
		chunkZ = bb.readInt();
		dim = bb.readInt();
		size = bb.readByte();
		types = new int[size * size];
		for(int i = 0; i < types.length; i++)
			types[i] = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(chunkX);
		bb.writeInt(chunkZ);
		bb.writeInt(dim);
		bb.writeByte(size);
		for(int i = 0; i < types.length; i++)
			bb.writeInt(types[i]);
	}
	
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageAreaUpdate m, MessageContext ctx)
	{ Minimap.get(m.dim).loadChunkTypes(m.chunkX, m.chunkZ, m.size, m.types); }
}