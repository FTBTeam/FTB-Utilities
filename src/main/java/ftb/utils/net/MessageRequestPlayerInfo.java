package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM_IO;
import ftb.utils.world.LMWorldServer;
import latmod.lib.ByteCount;

import java.util.UUID;

public class MessageRequestPlayerInfo extends MessageLM_IO
{
	public MessageRequestPlayerInfo() { super(ByteCount.BYTE); }
	
	public MessageRequestPlayerInfo(UUID id)
	{
		this();
		io.writeUUID(id);
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public IMessage onMessage(MessageContext ctx)
	{ return new MessageLMPlayerInfo(LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity), io.readUUID()); }
}