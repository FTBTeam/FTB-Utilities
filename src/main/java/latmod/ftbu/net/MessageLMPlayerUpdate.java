package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;
import latmod.lib.ByteCount;

public class MessageLMPlayerUpdate extends MessageFTBU
{
	public MessageLMPlayerUpdate() { super(ByteCount.INT); }
	
	public MessageLMPlayerUpdate(LMPlayerServer p, boolean self)
	{
		this();
		io.writeInt(p.playerID);
		p.writeToNet(io, self);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readInt());
		p.readFromNet(io, p.getUUID().equals(FTBLibClient.getUUID()));
		new EventLMPlayerClient.DataChanged(p).post();
		FTBLibClient.onGuiClientAction();
		return null;
	}
}