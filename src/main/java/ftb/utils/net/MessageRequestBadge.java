package ftb.utils.net;

import ftb.lib.api.ForgeWorldMP;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.badges.Badge;
import ftb.utils.badges.ServerBadges;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageRequestBadge extends MessageLM<MessageRequestBadge>
{
	public UUID playerID;
	
	public MessageRequestBadge() { }
	
	public MessageRequestBadge(UUID player)
	{
		playerID = player;
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
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
		Badge b = ServerBadges.getServerBadge(ForgeWorldMP.inst.getPlayer(m.playerID));
		if(b != Badge.emptyBadge) return new MessageSendBadge(m.playerID, b.getID());
		return null;
	}
}