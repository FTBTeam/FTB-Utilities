package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.api.guide.ServerInfoFile;
import ftb.utils.world.LMWorldServer;

public class MessageRequestServerInfo extends MessageLM
{
	public MessageRequestServerInfo() { super(null); }
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public IMessage onMessage(MessageContext ctx)
	{ return new ServerInfoFile(LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity)).displayGuide(null); }
}