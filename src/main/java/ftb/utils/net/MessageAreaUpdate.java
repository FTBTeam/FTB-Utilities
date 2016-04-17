package ftb.utils.net;

import ftb.lib.*;
import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.net.*;
import ftb.utils.world.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

public class MessageAreaUpdate extends MessageLM<MessageAreaUpdate>
{
	public DimensionType dim;
	public Map<ChunkDimPos, ChunkType> types;
	
	public MessageAreaUpdate() { }
	
	public MessageAreaUpdate(ForgePlayerMP p, int x, int z, DimensionType d, int sx, int sz)
	{
		dim = d;
		types = FTBUWorldDataMP.get().getChunkTypes(p, x, z, d, sx, sz);
	}
	
	public MessageAreaUpdate(ForgePlayerMP p, BlockDimPos pos, int radius)
	{ this(p, pos.chunkX() - radius, pos.chunkZ() - radius, pos.dim, radius * 2 + 1, radius * 2 + 1); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public void fromBytes(ByteBuf io)
	{
		dim = DimensionType.getById(io.readInt());
		int size = io.readInt();
		types = new HashMap<>(size);
		
		for(int i = 0; i < size; i++)
		{
			int x = io.readInt();
			int z = io.readInt();
			ChunkDimPos pos = new ChunkDimPos(dim, x, z);
			ChunkType type = ChunkType.read(pos, io);
			types.put(pos, type);
		}
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(dim.getId());
		io.writeInt(types.size());
		
		for(Map.Entry<ChunkDimPos, ChunkType> e : types.entrySet())
		{
			io.writeInt(e.getKey().chunkXPos);
			io.writeInt(e.getKey().chunkZPos);
			e.getValue().write(io);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{
		if(!FTBUWorldDataSP.get().isLoaded()) return null;
		FTBUWorldDataSP.get().setTypes(m.dim, m.types);
		return null;
	}
}