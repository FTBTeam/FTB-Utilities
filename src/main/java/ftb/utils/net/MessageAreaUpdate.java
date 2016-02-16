package ftb.utils.net;

import ftb.lib.BlockDimPos;
import ftb.lib.api.friends.LMPlayerMP;
import ftb.lib.api.net.*;
import ftb.utils.mod.client.gui.claims.ClaimedAreasClient;
import ftb.utils.world.claims.*;
import io.netty.buffer.ByteBuf;
import latmod.lib.MathHelperLM;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class MessageAreaUpdate extends MessageLM<MessageAreaUpdate>
{
	public int chunkX, chunkZ, dim, sizeX, sizeZ;
	public int types[];
	
	public MessageAreaUpdate() { }
	
	public MessageAreaUpdate(LMPlayerMP p, int x, int z, int d, int sx, int sz)
	{
		chunkX = x;
		chunkZ = z;
		dim = d;
		sizeX = MathHelperLM.clampInt(sx, 1, 255);
		sizeZ = MathHelperLM.clampInt(sz, 1, 255);
		types = new int[sizeX * sizeZ];
		
		int i = 0;
		for(int z1 = z; z1 < z + sz; z1++)
		{
			for(int x1 = x; x1 < x + sx; x1++)
			{
				ChunkType type = ClaimedChunks.instance.getType(d, x1, z1);
				if(type instanceof ChunkType.PlayerClaimed && type.isChunkOwner(p) && ClaimedChunks.instance.getChunk(d, x1, z1).isChunkloaded)
					type = ChunkType.LOADED_SELF;
				
				types[i] = type.ID;
				i++;
			}
		}
	}
	
	public MessageAreaUpdate(LMPlayerMP p, BlockDimPos pos, int sx, int sz)
	{ this(p, pos.chunkX() - (sx / 2 + 1), pos.chunkZ() - (sz / 2 + 1), pos.dim, sx, sz); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public void fromBytes(ByteBuf io)
	{
		chunkX = io.readInt();
		chunkZ = io.readInt();
		dim = io.readInt();
		sizeX = io.readUnsignedByte();
		sizeZ = io.readUnsignedByte();
		
		types = new int[sizeX * sizeZ];
		
		for(int i = 0; i < types.length; i++)
		{
			types[i] = io.readInt();
		}
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(chunkX);
		io.writeInt(chunkZ);
		io.writeInt(dim);
		io.writeByte(sizeX);
		io.writeByte(sizeZ);
		
		for(int i = 0; i < types.length; i++)
		{
			io.writeInt(types[i]);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{
		ClaimedAreasClient.setTypes(m.dim, m.chunkX, m.chunkZ, m.sizeX, m.sizeZ, m.types);
		return null;
	}
}