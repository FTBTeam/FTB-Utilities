package ftb.utils.net;

import ftb.lib.api.friends.*;
import ftb.lib.api.net.*;
import ftb.lib.mod.net.MessageLMPlayerUpdate;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageRequestSelfUpdate extends MessageLM<MessageRequestSelfUpdate>
{
	public MessageRequestSelfUpdate() { }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	public void fromBytes(ByteBuf io)
	{
	}
	
	public void toBytes(ByteBuf io)
	{
	}
	
	public IMessage onMessage(MessageRequestSelfUpdate m, MessageContext ctx)
	{
		LMPlayerMP owner = LMWorldMP.inst.getPlayer(ctx.getServerHandler().playerEntity);
		return new MessageLMPlayerUpdate(owner, true);
	}
}