package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;
import latmod.lib.ByteCount;

public class MessageLMPlayerLoggedOut extends MessageFTBU
{
	public MessageLMPlayerLoggedOut() { super(ByteCount.BYTE); }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
	{
		this();
		io.writeInt(p.playerID);
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