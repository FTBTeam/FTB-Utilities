package ftb.utils.net;

import ftb.lib.api.*;
import ftb.lib.api.net.*;
import ftb.utils.api.guide.*;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageRequestServerInfo extends MessageLM<MessageRequestServerInfo>
{
	public MessageRequestServerInfo() { }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	public void fromBytes(ByteBuf io)
	{
	}
	
	public void toBytes(ByteBuf io)
	{
	}
	
	public IMessage onMessage(MessageRequestServerInfo m, MessageContext ctx)
	{
		ForgePlayerMP owner = ForgeWorldMP.inst.getPlayer(ctx.getServerHandler().playerEntity);
		GuideFile.displayGuide(owner.getPlayer(), new ServerGuideFile(owner));
		return null;
	}
}