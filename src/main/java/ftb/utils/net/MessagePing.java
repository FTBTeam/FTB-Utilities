package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.net.LMNetworkWrapper;

public class MessagePing extends MessageFTBU
{
	public MessagePing() { super(null); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	public IMessage onMessage(MessageContext ctx)
	{ return new MessagePingResponse(); }
}