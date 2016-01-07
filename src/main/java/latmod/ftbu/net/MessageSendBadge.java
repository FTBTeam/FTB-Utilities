package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.badges.*;
import latmod.ftbu.world.LMWorldClient;
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
		if(LMWorldClient.inst == null) return null;
		int player = io.readInt();
		String badge = io.readUTF();
		Badge b = ClientBadges.loadedBadges.get(badge);
		if(b != null) ClientBadges.playerBadges.put(Integer.valueOf(player), b);
		return null;
	}
}