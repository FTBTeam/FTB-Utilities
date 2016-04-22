package ftb.utils.net;

import ftb.lib.api.*;
import ftb.lib.api.net.*;
import ftb.utils.api.guide.ServerGuideFile;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageRequestServerInfo extends MessageLM<MessageRequestServerInfo>
{
	public MessageRequestServerInfo() { }
	
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
	public IMessage onMessage(MessageRequestServerInfo m, MessageContext ctx)
	{
		ForgePlayerMP owner = ForgeWorldMP.inst.getPlayer(ctx.getServerHandler().playerEntity);
		new ServerGuideFile(owner).displayGuide(owner.getPlayer());
		return null;
	}
}