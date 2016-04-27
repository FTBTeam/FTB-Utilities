package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.BlockDimPos;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.mod.client.gui.claims.ClaimedAreasClient;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ChunkType;
import io.netty.buffer.ByteBuf;
import latmod.lib.MathHelperLM;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.HashMap;
import java.util.Map;

public class MessageAreaUpdate extends MessageLM<MessageAreaUpdate>
{
	public int dim;
	public Map<ChunkCoordIntPair, ChunkType> map;
	
	//Only on server side
	private LMPlayerServer player;
	
	public MessageAreaUpdate() { }
	
	public MessageAreaUpdate(LMPlayerServer p, int x, int z, int d, int sx, int sz)
	{
		player = p;
		
		sx = MathHelperLM.clampInt(sx, 1, 255);
		sz = MathHelperLM.clampInt(sz, 1, 255);
		
		dim = d;
		
		map = new HashMap<>();
		
		for(int z1 = z; z1 < z + sz; z1++)
		{
			for(int x1 = x; x1 < x + sx; x1++)
			{
				map.put(new ChunkCoordIntPair(x1, z1), LMWorldServer.inst.claimedChunks.getType(d, x1, z1));
			}
		}
	}
	
	public MessageAreaUpdate(LMPlayerServer p, BlockDimPos pos, int sx, int sz)
	{ this(p, pos.chunkX() - (sx / 2 + 1), pos.chunkZ() - (sz / 2 + 1), pos.dim, sx, sz); }
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		map = new HashMap<>();
		
		int s = io.readInt();
		
		for(int i = 0; i < s; i++)
		{
			int x = io.readInt();
			int z = io.readInt();
			ChunkCoordIntPair pos = new ChunkCoordIntPair(x, z);
			int id = io.readUnsignedByte();
			
			if(id == 5)
			{
				ChunkType.PlayerClaimed type = new ChunkType.PlayerClaimed(null);
				type.readFromNBT(readTag(io), dim, pos);
				map.put(pos, type);
			}
			else
			{
				map.put(pos, ChunkType.VALUES[id]);
			}
		}
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeInt(map.size());
		
		NBTTagCompound tag;
		
		for(Map.Entry<ChunkCoordIntPair, ChunkType> e : map.entrySet())
		{
			io.writeInt(e.getKey().chunkXPos);
			io.writeInt(e.getKey().chunkZPos);
			
			ChunkType t = e.getValue();
			io.writeByte(t.ID);
			
			if(t.ID == 5)
			{
				tag = new NBTTagCompound();
				t.writeToNBT(tag, player);
				writeTag(io, tag);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageAreaUpdate m, MessageContext ctx)
	{
		ClaimedAreasClient.setTypes(m.dim, m.map);
		return null;
	}
}