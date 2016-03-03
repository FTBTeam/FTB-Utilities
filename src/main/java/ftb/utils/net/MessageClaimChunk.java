package ftb.utils.net;

import ftb.lib.LMAccessToken;
import ftb.lib.api.net.*;
import ftb.lib.api.players.*;
import ftb.utils.world.*;
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
		LMPlayerMP p = LMWorldMP.inst.getPlayer(ctx.getServerHandler().playerEntity);
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(p);
		
		if(m.type == ID_CLAIM)
		{
			d.claimChunk(m.dim, m.posX, m.posZ);
			return new MessageAreaUpdate(p, m.posX, m.posZ, m.dim, 1, 1);
		}
		else if(m.type == ID_UNCLAIM)
		{
			if(m.token != 0L && LMAccessToken.equals(p.getPlayer(), m.token, false))
			{
				ClaimedChunk c = FTBUWorldDataMP.inst.getChunk(m.dim, m.posX, m.posZ);
				if(c != null)
				{
					LMPlayerMP p1 = LMWorldMP.inst.getPlayer(c.ownerID);
					FTBUPlayerDataMP d1 = FTBUPlayerDataMP.get(p1);
					d1.unclaimChunk(m.dim, m.posX, m.posZ);
				}
			}
			else d.unclaimChunk(m.dim, m.posX, m.posZ);
			return new MessageAreaUpdate(p, m.posX, m.posZ, m.dim, 1, 1);
		}
		else if(m.type == ID_UNCLAIM_ALL) d.unclaimAllChunks(m.dim);
		else if(m.type == ID_UNCLAIM_ALL_DIMS) d.unclaimAllChunks(null);
		else if(m.type == ID_LOAD) d.setLoaded(m.dim, m.posX, m.posZ, true);
		else if(m.type == ID_UNLOAD) d.setLoaded(m.dim, m.posX, m.posZ, false);
		return null;
	}
}