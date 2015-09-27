package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageClaimChunk extends MessageLM<MessageClaimChunk>
{
	public int chunkX, chunkZ, dim;
	public boolean claim;
	
	public MessageClaimChunk() { }
	
	public MessageClaimChunk(int d, int x, int z, boolean c)
	{
		dim = d;
		chunkX = x;
		chunkZ = z;
		claim = c;
	}
	
	public void readData(ByteIOStream io) throws Exception
	{
		dim = io.readInt();
		chunkX = io.readInt();
		chunkZ = io.readInt();
		claim = io.readBoolean();
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeInt(dim);
		io.writeInt(chunkX);
		io.writeInt(chunkZ);
		io.writeBoolean(claim);
	}
	
	public IMessage onMessage(MessageClaimChunk m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		if(m.claim) p.claims.claim(m.dim, m.chunkX, m.chunkZ);
		else p.claims.unclaim(m.dim, m.chunkX, m.chunkZ, false);
		return new MessageAreaUpdate(m.chunkX, m.chunkZ, m.dim, 1, p);
	}
}