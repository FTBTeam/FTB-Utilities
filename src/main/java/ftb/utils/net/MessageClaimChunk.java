package ftb.utils.net;

import ftb.lib.LMAccessToken;
import ftb.lib.api.*;
import ftb.lib.api.net.*;
import ftb.utils.world.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageClaimChunk extends MessageLM<MessageClaimChunk>
{
	public static final int ID_CLAIM = 0;
	public static final int ID_UNCLAIM = 1;
	public static final int ID_UNCLAIM_ALL = 2;
	public static final int ID_UNCLAIM_ALL_DIMS = 3;
	public static final int ID_LOAD = 4;
	public static final int ID_UNLOAD = 5;
	
	public int dim, type;
	public ChunkCoordIntPair pos;
	public long token;
	
	public MessageClaimChunk() { }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public void fromBytes(ByteBuf io)
	{
		type = io.readUnsignedByte();
		token = io.readLong();
		dim = io.readInt();
		pos = new ChunkCoordIntPair(io.readInt(), io.readInt());
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeByte(type);
		io.writeLong(token);
		io.writeInt(dim);
		io.writeInt(pos.chunkXPos);
		io.writeInt(pos.chunkZPos);
	}
	
	public IMessage onMessage(MessageClaimChunk m, MessageContext ctx)
	{
		ForgePlayerMP p = ForgeWorldMP.inst.getPlayer(ctx.getServerHandler().playerEntity);
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(p);
		
		if(m.type == ID_CLAIM)
		{
			d.claimChunk(m.dim, m.pos);
			return new MessageAreaUpdate(p, m.pos.chunkXPos, m.pos.chunkZPos, m.dim, 1, 1);
		}
		else if(m.type == ID_UNCLAIM)
		{
			if(m.token != 0L && LMAccessToken.equals(p.getPlayer(), m.token, false))
			{
				ClaimedChunk c = FTBUWorldDataMP.get().getChunk(m.dim, m.pos);
				if(c != null)
				{
					ForgePlayerMP p1 = ForgeWorldMP.inst.getPlayer(c.ownerID);
					FTBUPlayerDataMP d1 = FTBUPlayerDataMP.get(p1);
					d1.unclaimChunk(m.dim, m.pos);
				}
			}
			else d.unclaimChunk(m.dim, m.pos);
			return new MessageAreaUpdate(p, m.pos.chunkXPos, m.pos.chunkZPos, m.dim, 1, 1);
		}
		else if(m.type == ID_UNCLAIM_ALL) d.unclaimAllChunks(m.dim);
		else if(m.type == ID_UNCLAIM_ALL_DIMS) d.unclaimAllChunks(null);
		else if(m.type == ID_LOAD) d.setLoaded(m.dim, m.pos, true);
		else if(m.type == ID_UNLOAD) d.setLoaded(m.dim, m.pos, false);
		return null;
	}
}