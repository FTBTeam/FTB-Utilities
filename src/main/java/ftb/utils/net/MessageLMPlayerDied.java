package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.LMPlayer;
import ftb.utils.world.LMPlayerClient;
import ftb.utils.world.LMWorldClient;
import latmod.lib.ByteCount;

public class MessageLMPlayerDied extends MessageFTBU
{
	public MessageLMPlayerDied() { super(ByteCount.BYTE); }
	
	public MessageLMPlayerDied(LMPlayer p)
	{
		this();
		io.writeInt(p.getPlayerID());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readInt());
		if(p != null) new EventLMPlayerClient.PlayerDied(p).post();
		return null;
	}
}