package ftb.utils.net;

import ftb.lib.LMAccessToken;
import ftb.lib.api.net.*;
import ftb.utils.world.*;
import ftb.utils.world.claims.ClaimedChunk;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageClaimChunk extends MessageLM<MessageClaimChunk>
{
	public static final int ID_CLAIM = 0;
	public static final int ID_UNCLAIM = 1;
	public static final int ID_UNCLAIM_ALL = 2;
	public static final int ID_UNCLAIM_ALL_DIMS = 3;
	public static final int ID_LOAD = 4;
	public static final int ID_UNLOAD = 5;
	
	public int dim, posX, posZ, type;
	public long token;
	
	public MessageClaimChunk() { }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public void fromBytes(ByteBuf io)
	{
		type = io.readUnsignedByte();
		token = io.readLong();
		dim = io.readInt();
		posX = io.readInt();
		posZ = io.readInt();
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeByte(type);
		io.writeLong(token);
		io.writeInt(dim);
		io.writeInt(posX);
		io.writeInt(posZ);
	}
	
	public IMessage onMessage(MessageClaimChunk m, MessageContext ctx)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
		if(m.type == ID_CLAIM)
		{
			p.claimChunk(m.dim, m.posX, m.posZ);
			return new MessageAreaUpdate(p, m.posX, m.posZ, m.dim, 1, 1);
		}
		else if(m.type == ID_UNCLAIM)
		{
			if(m.token != 0L && LMAccessToken.equals(p.getPlayer(), m.token, false))
			{
				ClaimedChunk c = LMWorldServer.inst.claimedChunks.getChunk(m.dim, m.posX, m.posZ);
				if(c != null)
				{
					LMPlayerServer p1 = LMWorldServer.inst.getPlayer(c.ownerID);
					p1.unclaimChunk(m.dim, m.posX, m.posZ);
				}
			}
			else p.unclaimChunk(m.dim, m.posX, m.posZ);
			return new MessageAreaUpdate(p, m.posX, m.posZ, m.dim, 1, 1);
		}
		else if(m.type == ID_UNCLAIM_ALL) p.unclaimAllChunks(m.dim);
		else if(m.type == ID_UNCLAIM_ALL_DIMS) p.unclaimAllChunks(null);
		else if(m.type == ID_LOAD) p.setLoaded(m.dim, m.posX, m.posZ, true);
		else if(m.type == ID_UNLOAD) p.setLoaded(m.dim, m.posX, m.posZ, false);
		return null;
	}
}