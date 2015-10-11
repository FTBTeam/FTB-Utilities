package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.world.*;
import latmod.lib.MathHelperLM;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageClaimChunk extends MessageLM<MessageClaimChunk>
{
	public int chunkX, chunkZ, dim, sizeX, sizeZ;
	public boolean claim;
	
	public MessageClaimChunk() { }
	
	public MessageClaimChunk(int d, int x, int z, int sx, int sz, boolean c)
	{
		dim = d;
		chunkX = x;
		chunkZ = z;
		sizeX = MathHelperLM.clampInt(sx, 1, 255);
		sizeZ = MathHelperLM.clampInt(sz, 1, 255);
		claim = c;
	}
	
	public void fromBytes(ByteBuf io)
	{
		dim = io.readInt();
		chunkX = io.readInt();
		chunkZ = io.readInt();
		sizeX = io.readUnsignedByte();
		sizeZ = io.readUnsignedByte();
		claim = io.readBoolean();
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(dim);
		io.writeInt(chunkX);
		io.writeInt(chunkZ);
		io.writeByte(sizeX);
		io.writeByte(sizeZ);
		io.writeBoolean(claim);
	}
	
	public IMessage onMessage(MessageClaimChunk m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		if(m.claim) p.claims.claim(m.dim, m.chunkX, m.chunkZ, m.sizeX, m.sizeZ);
		else p.claims.unclaim(m.dim, m.chunkX, m.chunkZ, m.sizeX, m.sizeZ, false);
		return new MessageAreaUpdate(m.chunkX, m.chunkZ, m.dim, m.sizeX, m.sizeZ, p);
	}
}