package latmod.core.net;
import io.netty.buffer.ByteBuf;
import latmod.core.LMPlayer;
import latmod.core.mod.LC;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageLMPlayerLoggedOut extends MessageLM<MessageLMPlayerLoggedOut>
{
	public int playerID;
	
	public MessageLMPlayerLoggedOut() { }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
	{
		playerID = p.playerID;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
	}
	
	public IMessage onMessage(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{
		LMPlayer p = LMPlayer.getPlayer(m.playerID);
		LC.proxy.playerLMLoggedOut(p);
		return null;
	}
}