package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageClaimChunk extends MessageFTBU
{
	public MessageClaimChunk() { super(DATA_SHORT); }
	
	public MessageClaimChunk(int d, int x, int z, boolean c)
	{
		this();
		io.writeInt(d);
		io.writeInt(x);
		io.writeInt(z);
		io.writeBoolean(c);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public IMessage onMessage(MessageContext ctx)
	{
		int dim = io.readInt();
		int chunkX = io.readInt();
		int chunkZ = io.readInt();
		boolean claim = io.readBoolean();
		
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		if(claim) p.claims.claim(dim, chunkX, chunkZ);
		else p.claims.unclaim(dim, chunkX, chunkZ, false);
		return new MessageAreaUpdate(chunkX, chunkZ, dim, 1, 1, p);
	}
}