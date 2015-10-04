package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;

public class MessageLMPlayerLoggedOut extends MessageLM<MessageLMPlayerLoggedOut>
{
	public int playerID;
	
	public MessageLMPlayerLoggedOut() { }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
	{
		playerID = p.playerID;
	}
	
	public void fromBytes(ByteBuf io)
	{
		playerID = io.readInt();
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(playerID);
	}
	
	public IMessage onMessage(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		new EventLMPlayerClient.LoggedOut(p).post();
		p.isOnline = false;
		return null;
	}
}