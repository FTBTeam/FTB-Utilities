package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.FTBU;
import latmod.ftbu.core.LMPlayer;
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
		FTBU.proxy.playerLMLoggedOut(p);
		return null;
	}
}