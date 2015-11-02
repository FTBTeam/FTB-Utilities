package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.lib.MathHelperLM;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageAreaRequest extends MessageFTBU
{
	public MessageAreaRequest() { super(DATA_SHORT); }
	
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
		return new MessageAreaUpdate(chunkX, chunkY, ep.dimension, sizeX, sizeY);
	}
}