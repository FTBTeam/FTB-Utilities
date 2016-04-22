package ftb.utils.net;

import ftb.lib.api.net.*;
import ftb.utils.badges.ClientBadges;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

import java.util.UUID;

public class MessageSendBadge extends MessageLM<MessageSendBadge>
{
	public UUID playerID;
	public String badgeID;
	
	public MessageSendBadge() { }
	
	public MessageSendBadge(UUID player, String id)
	{
		playerID = player;
		badgeID = id;
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
		badgeID = readString(io);
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
		writeString(io, badgeID);
	}
	
	@Override
	public IMessage onMessage(MessageSendBadge m, MessageContext ctx)
	{
		ClientBadges.setClientBadge(m.playerID, m.badgeID);
		return null;
	}
}