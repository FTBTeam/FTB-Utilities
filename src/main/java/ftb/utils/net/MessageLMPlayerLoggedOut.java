package ftb.utils.net;

import ftb.lib.api.net.*;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.*;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class MessageLMPlayerLoggedOut extends MessageLM<MessageLMPlayerLoggedOut>
{
	public int playerID;
	
	public MessageLMPlayerLoggedOut() { }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
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
	public IMessage onMessage(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		new EventLMPlayerClient.LoggedOut(p).post();
		p.isOnline = false;
		return null;
	}
}