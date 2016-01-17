package latmod.ftbu.net;

import ftb.lib.api.net.LMNetworkWrapper;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class MessagePingResponse extends MessageFTBU
{
	public MessagePingResponse() { super(null); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		return null;
	}
}