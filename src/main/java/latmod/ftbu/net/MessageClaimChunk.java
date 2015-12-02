package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.world.*;
import latmod.ftbu.world.claims.*;

public class MessageClaimChunk extends MessageFTBU
{
	public static final int ID_CLAIM = 0;
	public static final int ID_UNCLAIM = 1;
	public static final int ID_UNCLAIM_ALL = 2;
	public static final int ID_UNCLAIM_ALL_DIMS = 3;
	public static final int ID_LOAD = 4;
	public static final int ID_UNLOAD = 5;
	
	public MessageClaimChunk() { super(DATA_SHORT); }
	
	public MessageClaimChunk(int d, long t, int x, int z, int c)
	{
		this();
		io.writeUByte(c);
		io.writeLong(t);
		io.writeInt(d);
		io.writeInt(x);
		io.writeInt(z);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public IMessage onMessage(MessageContext ctx)
	{
		int type = io.readUByte();
		long token = io.readLong();
		int dim = io.readInt();
		int cx = io.readInt();
		int cz = io.readInt();
		
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
		if(type == ID_CLAIM)
		{
			p.claims.claim(dim, cx, cz);
			return new MessageAreaUpdate(cx, cz, dim, 1, 1);
		}
		else if(type == ID_UNCLAIM)
		{
			if(token != 0L && AdminToken.equals(p.getPlayer(), token))
			{
				ClaimedChunk c = Claims.get(dim, cx, cz);
				if(c != null) c.claims.unclaim(dim, cx, cz);
			}
			else
				p.claims.unclaim(dim, cx, cz);			
			return new MessageAreaUpdate(cx, cz, dim, 1, 1);
		}
		else if(type == ID_UNCLAIM_ALL) p.claims.unclaimAll(dim);
		else if(type == ID_UNCLAIM_ALL_DIMS) p.claims.unclaimAll();
		else if(type == ID_LOAD) p.claims.loadChunk(LMDimUtils.getWorld(dim), cx, cz);
		else if(type == ID_UNLOAD) p.claims.unloadChunk(LMDimUtils.getWorld(dim), cx, cz);
		return null;
	}
}