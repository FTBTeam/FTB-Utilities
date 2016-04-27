package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.badges.Badge;
import ftb.utils.badges.ServerBadges;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class MessageRequestBadge extends MessageLM<MessageRequestBadge>
{
	public UUID playerID;
	
	public MessageRequestBadge() { }
	
	public MessageRequestBadge(UUID id)
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
	public IMessage onMessage(MessageRequestBadge m, MessageContext ctx)
	{
		Badge b = ServerBadges.getServerBadge(m.playerID);
		return (b == null || b == Badge.emptyBadge) ? null : new MessageSendBadge(m.playerID, b.getID());
	}
}