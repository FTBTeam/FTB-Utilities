package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.EntityPos;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.mod.client.gui.claims.ClaimedAreasClient;
import latmod.ftbu.world.LMWorldServer;
import latmod.lib.MathHelperLM;

public class MessageAreaUpdate extends MessageFTBU
{
	public MessageAreaUpdate() { super(DATA_LONG); }
	
	public MessageAreaUpdate(int x, int z, int d, int sx, int sz)
	{
		this();
		sx = MathHelperLM.clampInt(sx, 1, 255);
		sz = MathHelperLM.clampInt(sx, 1, 255);
		
		io.writeInt(x);
		io.writeInt(z);
		io.writeInt(d);
		io.writeUByte(sx);
		io.writeUByte(sz);
		
		for(int z1 = z; z1 < z + sz; z1++) for(int x1 = x; x1 < x + sx; x1++)
			io.writeInt(LMWorldServer.inst.claimedChunks.getType(d, x1, z1).ID);
	}
	
	public MessageAreaUpdate(EntityPos pos, int sx, int sz)
	{ this(MathHelperLM.chunk(pos.x) - (sx / 2 + 1), MathHelperLM.chunk(pos.z) - (sz / 2 + 1), pos.dim, sx, sz); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		int chunkX = io.readInt();
		int chunkZ = io.readInt();
		int dim = io.readInt();
		int sx = io.readUByte();
		int sz = io.readUByte();
		
		int[] types = new int[sx * sz];
		for(int i = 0; i < types.length; i++)
			types[i]  = io.readInt();
		
		ClaimedAreasClient.setTypes(dim, chunkX, chunkZ, sx, sz, types);
		return null;
	}
}