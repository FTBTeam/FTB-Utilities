package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageClaimChunk extends MessageFTBU
{
	public static final int ID_CLAIM = 0;
	public static final int ID_UNCLAIM = 1;
	public static final int ID_UNCLAIM_ALL = 2;
	public static final int ID_UNCLAIM_ALL_DIMS = 3;
	
	public MessageClaimChunk() { super(DATA_SHORT); }
	
	public MessageClaimChunk(int d, int x, int z, int c)
	{
		this();
		io.writeUByte(c);
		io.writeInt(d);
		io.writeInt(x);
		io.writeInt(z);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public IMessage onMessage(MessageContext ctx)
	{
		int type = io.readUByte();
		int dim = io.readInt();
		int chunkX = io.readInt();
		int chunkZ = io.readInt();
		
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		if(type == ID_CLAIM)
		{
			p.claims.claim(dim, chunkX, chunkZ);
			return new MessageAreaUpdate(chunkX, chunkZ, dim, 1, 1);
		}
		else if(type == ID_UNCLAIM)
		{
			p.claims.unclaim(dim, chunkX, chunkZ, false);
			return new MessageAreaUpdate(chunkX, chunkZ, dim, 1, 1);
		}
		else if(type == ID_UNCLAIM_ALL) p.claims.unclaimAll(dim);
		else if(type == ID_UNCLAIM_ALL_DIMS) p.claims.unclaimAll();
		return null;
	}
}