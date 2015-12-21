package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.LMNetworkWrapper;

public class MessagePing extends MessageFTBU
{
	public MessagePing() { super(null); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	public IMessage onMessage(MessageContext ctx)
	{ return new MessagePingResponse(); }
}