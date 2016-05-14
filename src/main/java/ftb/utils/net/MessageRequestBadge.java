package ftb.utils.net;

import ftb.lib.api.ForgeWorldMP;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageToServer;
import ftb.utils.badges.Badge;
import ftb.utils.badges.ServerBadges;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class MessageRequestBadge extends MessageToServer<MessageRequestBadge>
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
	public void onMessage(MessageRequestBadge m, EntityPlayerMP ep)
	{
		Badge b = ServerBadges.getServerBadge(ForgeWorldMP.inst.getPlayer(m.playerID));
		
		if(b != Badge.emptyBadge)
		{
			new MessageSendBadge(m.playerID, b.getID()).sendTo(ep);
		}
	}
}