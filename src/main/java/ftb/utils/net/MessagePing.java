package ftb.utils.net;

import ftb.lib.api.net.*;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessagePing extends MessageLM<MessagePing>
{
	public MessagePing() { }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	public void fromBytes(ByteBuf io)
	{
	}
	
	public void toBytes(ByteBuf io)
	{
	}
	
	public IMessage onMessage(MessagePing m, MessageContext ctx)
	{ return new MessagePingResponse(); }
}