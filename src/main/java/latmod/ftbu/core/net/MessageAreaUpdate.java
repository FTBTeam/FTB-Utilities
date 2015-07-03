package latmod.ftbu.core.net;

import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.claims.ChunkType;
import latmod.ftbu.mod.client.minimap.Minimap;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageAreaUpdate extends MessageLM<MessageAreaUpdate> implements IClientMessageLM<MessageAreaUpdate> //MessageAreaRequest
{
	public int chunkX, chunkZ, dim;
	public byte size;
	public byte[] types;
	
	public MessageAreaUpdate() { }
	
	public MessageAreaUpdate(int x, int z, int d, byte s, byte[] b)
	{
		chunkX = x; chunkZ = z; dim = d; size = s; types = b; }
	
	public MessageAreaUpdate(int x, int z, int d, byte s, LMPlayer p)
	{
		this(x, z, d, s, new byte[s * s]);
		for(int z1 = 0; z1 < s; z1++) for(int x1 = 0; x1 < s; x1++)
			types[x1 + z1 * s] = (byte)ChunkType.get(d, x + x1, z + z1, p).ordinal();
	}
	
	public void fromBytes(ByteBuf bb)
	{
		chunkX = bb.readInt();
		chunkZ = bb.readInt();
		dim = bb.readInt();
		size = bb.readByte();
		types = new byte[size * size];
		bb.readBytes(types);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(chunkX);
		bb.writeInt(chunkZ);
		bb.writeInt(dim);
		bb.writeByte(size);
		bb.writeBytes(types);
	}
	
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageAreaUpdate m, MessageContext ctx)
	{ Minimap.get(m.dim).loadChunkTypes(m.chunkX, m.chunkZ, m.size, m.types); }
}