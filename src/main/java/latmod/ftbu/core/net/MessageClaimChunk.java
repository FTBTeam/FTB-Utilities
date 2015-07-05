package latmod.ftbu.core.net;

import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.claims.ChunkType;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageClaimChunk extends MessageLM<MessageClaimChunk>
{
	public int chunkX, chunkZ, dim, claim;
	
	public MessageClaimChunk() { }
	
	public MessageClaimChunk(int d, int x, int z, int c)
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
		claim = bb.readByte();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(dim);
		bb.writeInt(chunkX);
		bb.writeInt(chunkZ);
		bb.writeByte(claim);
	}
	
	public IMessage onMessage(MessageClaimChunk m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer p = LMWorld.server.getPlayer(ep);
		if(m.claim % 2 == 0) p.claims.unclaim(m.dim, m.chunkX, m.chunkZ, m.claim > 1);
		else if(m.claim % 2 == 1) p.claims.claim(m.dim, m.chunkX, m.chunkZ, m.claim > 1);
		
		byte[] types = new byte[1];
		types[0] = (byte)ChunkType.get(m.dim, m.chunkX, m.chunkZ, p).ordinal();
		return new MessageAreaUpdate(m.chunkX, m.chunkZ, m.dim, (byte)1, types);
	}
}