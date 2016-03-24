package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.badges.ClientBadges;
import latmod.lib.ByteCount;

import java.util.UUID;

public class MessageSendBadge extends MessageFTBU
{
	public MessageSendBadge() { super(ByteCount.BYTE); }
	
	public MessageSendBadge(UUID player, String id)
	{
		this();
		io.writeUUID(player);
		io.writeUTF(id);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		UUID player = io.readUUID();
		String badge = io.readUTF();
		ClientBadges.setClientBadge(player, badge);
		return null;
	}
}