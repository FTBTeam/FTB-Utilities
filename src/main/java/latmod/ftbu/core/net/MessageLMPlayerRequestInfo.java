package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.world.*;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageLMPlayerRequestInfo extends MessageLM<MessageLMPlayerRequestInfo>
{
	public int playerID;
	
	public MessageLMPlayerRequestInfo() { }
	
	public MessageLMPlayerRequestInfo(LMPlayer p)
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
	
	public IMessage onMessage(MessageLMPlayerRequestInfo m, MessageContext ctx)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(m.playerID);
		return (p == null) ? null : p.getInfo();
	}
}