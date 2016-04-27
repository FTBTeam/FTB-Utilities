package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.api.guide.ServerInfoFile;
import ftb.utils.world.LMWorldServer;
import io.netty.buffer.ByteBuf;

public class MessageRequestServerInfo extends MessageLM<MessageRequestServerInfo>
{
	public MessageRequestServerInfo() { }
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
	}
	
	@Override
	public IMessage onMessage(MessageRequestServerInfo m, MessageContext ctx)
	{
		return new ServerInfoFile(LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity)).displayGuide(null);
	}
}