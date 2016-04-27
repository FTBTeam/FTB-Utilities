package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.world.LMWorldServer;
import io.netty.buffer.ByteBuf;

public class MessageRequestSelfUpdate extends MessageLM<MessageRequestSelfUpdate>
{
	public MessageRequestSelfUpdate() { }
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
	}
	
	@Override
	public IMessage onMessage(MessageRequestSelfUpdate m, MessageContext ctx)
	{
		return new MessageLMPlayerUpdate(LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity), true);
	}
}