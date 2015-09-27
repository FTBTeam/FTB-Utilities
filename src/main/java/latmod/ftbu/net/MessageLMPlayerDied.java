package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;

public class MessageLMPlayerDied extends MessageLM<MessageLMPlayerDied>
{
	public int playerID;
	
	public MessageLMPlayerDied() { }
	
	public MessageLMPlayerDied(LMPlayer p)
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
	
	public IMessage onMessage(MessageLMPlayerDied m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		if(p != null) new EventLMPlayerClient.PlayerDied(p).post();
		return null;
	}
}