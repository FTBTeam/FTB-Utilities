package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.world.LMWorldServer;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class MessageRequestPlayerInfo extends MessageLM<MessageRequestPlayerInfo>
{
	public UUID playerID;
	
	public MessageRequestPlayerInfo() { }
	
	public MessageRequestPlayerInfo(UUID id)
	{
		playerID = id;
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
	}
	
	@Override
	public IMessage onMessage(MessageRequestPlayerInfo m, MessageContext ctx)
	{
		return new MessageLMPlayerInfo(LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity), m.playerID);
	}
}