package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM_IO;
import ftb.utils.badges.ClientBadges;
import latmod.lib.ByteCount;

import java.util.UUID;

public class MessageSendBadge extends MessageLM_IO
{
	public MessageSendBadge() { super(ByteCount.BYTE); }
	
	public MessageSendBadge(UUID player, String id)
	{
		this();
		io.writeUUID(player);
		io.writeUTF(id);
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		UUID player = io.readUUID();
		String badge = io.readUTF();
		ClientBadges.setClientBadge(player, badge);
		return null;
	}
}