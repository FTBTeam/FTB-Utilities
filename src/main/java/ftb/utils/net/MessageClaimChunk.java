package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.LMAccessToken;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ClaimedChunk;
import io.netty.buffer.ByteBuf;

public class MessageClaimChunk extends MessageLM<MessageClaimChunk>
{
	public enum ID
	{
		CLAIM,
		UNCLAIM,
		UNCLAIM_ALL,
		UNCLAIM_ALL_DIMS,
		LOAD,
		UNLOAD
	}
	
	public long token;
	public int typeID, dim, cx, cz;
	
	public MessageClaimChunk() { }
	
	public MessageClaimChunk(ID id, long t, int d, int x, int z)
	{
		typeID = id.ordinal();
		token = t;
		dim = d;
		cx = x;
		cz = z;
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		typeID = io.readUnsignedByte();
		token = io.readLong();
		dim = io.readInt();
		cx = io.readInt();
		cz = io.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeByte(typeID);
		io.writeLong(token);
		io.writeInt(dim);
		io.writeInt(cx);
		io.writeInt(cz);
	}
	
	@Override
	public IMessage onMessage(MessageClaimChunk m, MessageContext ctx)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
		
		switch(ID.values()[m.typeID])
		{
			case CLAIM:
			{
				p.claimChunk(m.dim, m.cx, m.cz);
				return new MessageAreaUpdate(p, m.cx, m.cz, m.dim, 1, 1);
			}
			case UNCLAIM:
			{
				if(m.token != 0L && LMAccessToken.equals(p.getPlayer(), m.token, false))
				{
					ClaimedChunk c = LMWorldServer.inst.claimedChunks.getChunk(m.dim, m.cx, m.cz);
					if(c != null)
					{
						LMPlayerServer p1 = LMWorldServer.inst.getPlayer(c.ownerID);
						p1.unclaimChunk(m.dim, m.cx, m.cz);
					}
				}
				else p.unclaimChunk(m.dim, m.cx, m.cz);
				return new MessageAreaUpdate(p, m.cx, m.cz, m.dim, 1, 1);
			}
			case UNCLAIM_ALL:
			{
				p.unclaimAllChunks(Integer.valueOf(m.dim));
			}
			case UNCLAIM_ALL_DIMS:
			{
				p.unclaimAllChunks(null);
			}
			case LOAD:
			{
				p.setLoaded(m.dim, m.cx, m.cz, true);
			}
			case UNLOAD:
			{
				p.setLoaded(m.dim, m.cx, m.cz, false);
			}
		}
		
		return null;
	}
}