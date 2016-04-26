package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM_IO;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.LMPlayer;
import ftb.utils.world.LMPlayerClient;
import ftb.utils.world.LMWorldClient;
import latmod.lib.ByteCount;

public class MessageLMPlayerLoggedOut extends MessageLM_IO
{
	public MessageLMPlayerLoggedOut() { super(ByteCount.BYTE); }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
	{
		this();
		io.writeUUID(p.getProfile().getId());
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readUUID());
		new EventLMPlayerClient.LoggedOut(p).post();
		p.isOnline = false;
		return null;
	}
}