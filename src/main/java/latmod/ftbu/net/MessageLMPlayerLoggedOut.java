package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
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
	
	public void readData(ByteIOStream io) throws Exception
	{
		playerID = io.readInt();
	}
	
	public void writeData(ByteIOStream io) throws Exception
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