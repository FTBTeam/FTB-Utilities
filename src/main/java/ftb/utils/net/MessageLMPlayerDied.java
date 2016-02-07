package ftb.utils.net;

import ftb.lib.api.net.*;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.*;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class MessageLMPlayerDied extends MessageLM<MessageLMPlayerDied>
{
	public int playerID;
	
	public MessageLMPlayerDied() { }
	
	public MessageLMPlayerDied(LMPlayer p)
	{
		playerID = p.getPlayerID();
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
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