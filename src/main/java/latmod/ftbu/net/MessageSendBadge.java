package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.badges.ClientBadges;
import latmod.lib.ByteCount;

public class MessageSendBadge extends MessageFTBU
{
	public MessageSendBadge() { super(ByteCount.BYTE); }

	public MessageSendBadge(int player, String id)
	{
		this();
		io.writeInt(player);
		io.writeUTF(id);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		int player = io.readInt();
		String badge = io.readUTF();
		ClientBadges.setClientBadge(player, badge);
		return null;
	}
}