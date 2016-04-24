package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.world.LMWorldServer;

public class MessageRequestSelfUpdate extends MessageLM
{
	public MessageRequestSelfUpdate() { super(null); }
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public IMessage onMessage(MessageContext ctx)
	{ return new MessageLMPlayerUpdate(LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity), true); }
}