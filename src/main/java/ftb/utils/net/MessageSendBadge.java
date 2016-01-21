package ftb.utils.net;

import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.badges.ClientBadges;
import latmod.lib.ByteCount;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

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