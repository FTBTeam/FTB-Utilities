package ftb.utils.net;

import ftb.lib.BlockDimPos;
import ftb.lib.api.net.*;
import ftb.lib.api.players.LMPlayerMP;
import ftb.utils.world.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

public class MessageAreaUpdate extends MessageLM<MessageAreaUpdate>
{
	public int dim;
	public Map<ChunkCoordIntPair, ChunkType> types;
	
	public MessageAreaUpdate() { }
	
	public MessageAreaUpdate(LMPlayerMP p, int x, int z, int d, int sx, int sz)
	{
		dim = d;
		types = FTBUWorldDataMP.inst.getChunkTypes(p, x, z, d, sx, sz);
	}
	
	public MessageAreaUpdate(LMPlayerMP p, BlockDimPos pos, int radius)
	{ this(p, pos.chunkX() - radius, pos.chunkZ() - radius, pos.dim, radius * 2 + 1, radius * 2 + 1); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public void fromBytes(ByteBuf io)
	{
		dim = io.readInt();
		int size = io.readInt();
		types = new HashMap<>(size);
		
		for(int i = 0; i < size; i++)
		{
			int x = io.readInt();
			int z = io.readInt();
			ChunkCoordIntPair pos = new ChunkCoordIntPair(x, z);
			ChunkType type = ChunkType.read(dim, pos, io);
			types.put(pos, type);
		}
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(dim);
		io.writeInt(types.size());
		
		for(Map.Entry<ChunkCoordIntPair, ChunkType> e : types.entrySet())
		{
			io.writeInt(e.getKey().chunkXPos);
			io.writeInt(e.getKey().chunkZPos);
			e.getValue().write(io);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{
		if(FTBUWorldDataSP.inst == null) return null;
		FTBUWorldDataSP.inst.setTypes(m.dim, m.types);
		return null;
	}
}