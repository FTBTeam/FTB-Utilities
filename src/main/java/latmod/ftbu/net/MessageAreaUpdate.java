package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.client.minimap.Minimap;
import latmod.ftbu.util.EntityPos;
import latmod.ftbu.world.*;
import latmod.lib.MathHelperLM;

public class MessageAreaUpdate extends MessageByteArray<MessageAreaUpdate>
{
	public MessageAreaUpdate() { }
	
	public MessageAreaUpdate(int x, int z, int d, int sx, int sz, LMPlayerServer p)
	{
		sx = MathHelperLM.clampInt(sx, 1, 256);
		sz = MathHelperLM.clampInt(sx, 1, 256);
		
		io.writeInt(x);
		io.writeInt(z);
		io.writeInt(d);
		io.writeUByte(sx - 1);
		io.writeUByte(sz - 1);
		
		for(int z1 = 0; z1 < sz; z1++) for(int x1 = 0; x1 < sz; x1++)
			io.writeInt(ChunkType.getChunkTypeI(d, x + x1, z + z1, p));
	}
	
	public MessageAreaUpdate(EntityPos pos, int sx, int sz, LMPlayerServer p)
	{ this(MathHelperLM.chunk(pos.x) - (sx / 2 + 1), MathHelperLM.chunk(pos.z) - (sz / 2 + 1), pos.dim, sx, sz, p); }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{
		int chunkX = m.io.readInt();
		int chunkZ = m.io.readInt();
		int dim = m.io.readInt();
		int sx = m.io.readUByte() + 1;
		int sz = m.io.readUByte() + 1;
		
		int[] types = new int[sx * sz];
		for(int i = 0; i < types.length; i++)
			types[i]  = m.io.readInt();
		
		Minimap.get(dim).loadChunkTypes(chunkX, chunkZ, sz, sz, types);
		return null;
	}
}