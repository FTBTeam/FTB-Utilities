package ftb.utils.net;

import ftb.lib.api.net.*;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class MessagePingResponse extends MessageLM<MessagePingResponse>
{
	public MessagePingResponse() { }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	public void fromBytes(ByteBuf io)
	{
	}
	
	public void toBytes(ByteBuf io)
	{
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessagePingResponse m, MessageContext ctx)
	{
		return null;
	}
}