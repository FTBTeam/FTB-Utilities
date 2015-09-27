package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.mod.client.minimap.Minimap;
import latmod.ftbu.world.*;

public class MessageAreaUpdate extends MessageLM<MessageAreaUpdate> //MessageAreaRequest
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
	
	public void readData(ByteIOStream io) throws Exception
	{
		chunkX = io.readInt();
		chunkZ = io.readInt();
		dim = io.readInt();
		size = io.readUByte();
		types = new int[size * size];
		for(int i = 0; i < types.length; i++)
			types[i] = io.readInt();
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeInt(chunkX);
		io.writeInt(chunkZ);
		io.writeInt(dim);
		io.writeUByte(size);
		for(int i = 0; i < types.length; i++)
			io.writeInt(types[i]);
	}
	
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{
		Minimap.get(m.dim).loadChunkTypes(m.chunkX, m.chunkZ, m.size, m.types);
		return null;
	}
}