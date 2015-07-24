package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.world.LMWorldServer;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageLMPlayerRequestInfo extends MessageLM<MessageLMPlayerRequestInfo>
{
	public int playerID;
	
	public MessageLMPlayerRequestInfo() { }
	
	public MessageLMPlayerRequestInfo(int pid)
	{ playerID = pid; }
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
	}
	
	public IMessage onMessage(MessageLMPlayerRequestInfo m, MessageContext ctx)
	{ return new MessageLMPlayerInfo(LMWorldServer.inst.getPlayer(m.playerID)); }
}