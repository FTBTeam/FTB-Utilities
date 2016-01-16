package latmod.ftbu.net;

import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.world.LMWorldServer;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageAreaRequest extends MessageFTBU
{
	public MessageAreaRequest() { super(ByteCount.BYTE); }
	
	public MessageAreaRequest(int x, int y, int w, int h)
	{
		this();
		io.writeInt(x);
		io.writeInt(y);
		io.writeInt(MathHelperLM.clampInt(w, 1, 255));
		io.writeInt(MathHelperLM.clampInt(h, 1, 255));
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_WORLD; }
	
	public IMessage onMessage(MessageContext ctx)
	{
		int chunkX = io.readInt();
		int chunkY = io.readInt();
		int sizeX = io.readInt();
		int sizeY = io.readInt();
		
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		return new MessageAreaUpdate(LMWorldServer.inst.getPlayer(ep), chunkX, chunkY, ep.dimension, sizeX, sizeY);
	}
}