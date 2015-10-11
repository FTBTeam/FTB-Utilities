package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
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
	
	public void fromBytes(ByteBuf io)
	{
		playerID = io.readInt();
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(playerID);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerDied m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		if(p != null) new EventLMPlayerClient.PlayerDied(p).post();
		return null;
	}
}