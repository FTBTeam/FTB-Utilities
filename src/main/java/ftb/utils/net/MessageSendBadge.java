package ftb.utils.net;

import ftb.lib.api.net.*;
import ftb.utils.badges.ClientBadges;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageSendBadge extends MessageLM<MessageSendBadge>
{
	public int playerID;
	public String badgeID;
	
	public MessageSendBadge() { }
	
	public MessageSendBadge(int player, String id)
	{
		playerID = player;
		badgeID = id;
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	public void fromBytes(ByteBuf io)
	{
		playerID = io.readInt();
		badgeID = ByteBufUtils.readUTF8String(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(playerID);
		ByteBufUtils.writeUTF8String(io, badgeID);
	}
	
	public IMessage onMessage(MessageSendBadge m, MessageContext ctx)
	{
		ClientBadges.setClientBadge(m.playerID, m.badgeID);
		return null;
	}
}