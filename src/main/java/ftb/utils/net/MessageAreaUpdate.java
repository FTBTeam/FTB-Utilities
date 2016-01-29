package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.BlockDimPos;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.mod.client.gui.claims.ClaimedAreasClient;
import ftb.utils.world.*;
import ftb.utils.world.claims.ChunkType;
import latmod.lib.*;

public class MessageAreaUpdate extends MessageFTBU
{
	public MessageAreaUpdate() { super(ByteCount.INT); }
	
	public MessageAreaUpdate(LMPlayerServer p, int x, int z, int d, int sx, int sz)
	{
		this();
		sx = MathHelperLM.clampInt(sx, 1, 255);
		sz = MathHelperLM.clampInt(sx, 1, 255);
		
		io.writeInt(x);
		io.writeInt(z);
		io.writeInt(d);
		io.writeByte(sx);
		io.writeByte(sz);
		
		for(int z1 = z; z1 < z + sz; z1++)
			for(int x1 = x; x1 < x + sx; x1++)
			{
				ChunkType type = LMWorldServer.inst.claimedChunks.getType(d, x1, z1);
				if(type instanceof ChunkType.PlayerClaimed && type.isChunkOwner(p) && LMWorldServer.inst.claimedChunks.getChunk(d, x1, z1).isChunkloaded)
					type = ChunkType.LOADED_SELF;
				io.writeInt(type.ID);
			}
	}
	
	public MessageAreaUpdate(LMPlayerServer p, BlockDimPos pos, int sx, int sz)
	{ this(p, pos.chunkX() - (sx / 2 + 1), pos.chunkZ() - (sz / 2 + 1), pos.dim, sx, sz); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		int chunkX = io.readInt();
		int chunkZ = io.readInt();
		int dim = io.readInt();
		int sx = io.readUnsignedByte();
		int sz = io.readUnsignedByte();
		
		int[] types = new int[sx * sz];
		for(int i = 0; i < types.length; i++)
			types[i] = io.readInt();
		
		ClaimedAreasClient.setTypes(dim, chunkX, chunkZ, sx, sz, types);
		return null;
	}
}