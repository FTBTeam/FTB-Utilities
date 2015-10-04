package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.core.util.MathHelperLM;
import latmod.ftbu.mod.client.minimap.Minimap;
import latmod.ftbu.util.EntityPos;
import latmod.ftbu.world.*;

public class MessageAreaUpdate extends MessageLM<MessageAreaUpdate> //MessageAreaRequest
{
	public int chunkX, chunkZ, dim, sizeX, sizeZ;
	public int[] types;
	
	public MessageAreaUpdate() { }
	
	public MessageAreaUpdate(int x, int z, int d, int sx, int sz, LMPlayerServer p)
	{
		chunkX = x;
		chunkZ = z;
		dim = d;
		sizeX = MathHelperLM.clampInt(sx, 1, 255);
		sizeZ = MathHelperLM.clampInt(sz, 1, 255);
		types = new int[sx * sz];
		
		for(int z1 = 0; z1 < sizeZ; z1++) for(int x1 = 0; x1 < sizeX; x1++)
			types[x1 + z1 * sizeX] = ChunkType.getChunkTypeI(dim, x + x1, z + z1, p);
	}
	
	public MessageAreaUpdate(EntityPos pos, int sx, int sz, LMPlayerServer p)
	{ this(MathHelperLM.chunk(pos.x) - (sx / 2 + 1), MathHelperLM.chunk(pos.z) - (sz / 2 + 1), pos.dim, sx, sz, p); }
	
	public void fromBytes(ByteBuf io)
	{
		chunkX = io.readInt();
		chunkZ = io.readInt();
		dim = io.readInt();
		sizeX = io.readUnsignedByte();
		sizeZ = io.readUnsignedByte();
		types = new int[sizeX * sizeZ];
		for(int i = 0; i < types.length; i++)
			types[i] = io.readInt();
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(chunkX);
		io.writeInt(chunkZ);
		io.writeInt(dim);
		io.writeByte(sizeX);
		io.writeByte(sizeZ);
		for(int i = 0; i < types.length; i++)
			io.writeInt(types[i]);
	}
	
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{
		Minimap.get(m.dim).loadChunkTypes(m.chunkX, m.chunkZ, m.sizeX, m.sizeZ, m.types);
		return null;
	}
}