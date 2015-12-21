package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;
import latmod.lib.ByteCount;

public class MessageLMPlayerDied extends MessageFTBU
{
	public MessageLMPlayerDied() { super(ByteCount.BYTE); }
	
	public MessageLMPlayerDied(LMPlayer p)
	{
		this();
		io.writeInt(p.playerID);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readInt());
		if(p != null) new EventLMPlayerClient.PlayerDied(p).post();
		return null;
	}
}