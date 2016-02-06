package ftb.utils.net;

import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.*;
import latmod.lib.ByteCount;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class MessageLMPlayerLoggedOut extends MessageFTBU
{
	public MessageLMPlayerLoggedOut() { super(ByteCount.BYTE); }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
	{
		this();
		io.writeInt(p.getPlayerID());
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readInt());
		new EventLMPlayerClient.LoggedOut(p).post();
		p.isOnline = false;
		return null;
	}
}