package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageAreaRequest extends MessageFTBU
{
	public MessageAreaRequest() { super(DATA_SHORT); }
	
	public MessageAreaRequest(int x, int y, int w, int h)
	{
		this();
		io.writeInt(x);
		io.writeInt(y);
		io.writeByte((byte)w);
		io.writeByte((byte)h);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_CLAIMS; }
	
	public IMessage onMessage(MessageContext ctx)
	{
		int chunkX = io.readInt();
		int chunkY = io.readInt();
		int sizeX = io.readByte() & 0xFF;
		int sizeY = io.readByte() & 0xFF;
		
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer owner = LMWorldServer.inst.getPlayer(ep);
		return new MessageAreaUpdate(chunkX, chunkY, ep.dimension, sizeX, sizeY, owner);
	}
}