package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.badges.ClientBadges;
import io.netty.buffer.ByteBuf;

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
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageSendBadge m, MessageContext ctx)
	{
		ClientBadges.setClientBadge(m.playerID, m.badgeID);
		return null;
	}
}