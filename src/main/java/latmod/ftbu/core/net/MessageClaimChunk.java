package latmod.ftbu.core.net;

import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.world.*;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.*;

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
	
	public void fromBytes(ByteBuf bb)
	{
		dim = bb.readInt();
		chunkX = bb.readInt();
		chunkZ = bb.readInt();
		claim = bb.readBoolean();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(dim);
		bb.writeInt(chunkX);
		bb.writeInt(chunkZ);
		bb.writeBoolean(claim);
	}
	
	public IMessage onMessage(MessageClaimChunk m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer p = LMWorld.server.getPlayer(ep);
		if(m.claim) p.claims.claim(m.dim, m.chunkX, m.chunkZ);
		else p.claims.unclaim(m.dim, m.chunkX, m.chunkZ, false);
		return new MessageAreaUpdate(m.chunkX, m.chunkZ, m.dim, (byte)1, p);
	}
}