package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.*;
import latmod.lib.ByteCount;

public class MessageLMPlayerUpdate extends MessageFTBU
{
	public MessageLMPlayerUpdate() { super(ByteCount.INT); }
	
	public MessageLMPlayerUpdate(LMPlayerServer p, boolean self)
	{
		this();
		io.writeInt(p.playerID);
		io.writeBoolean(self);
		p.writeToNet(io, self);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readInt());
		boolean self = io.readBoolean();
		p.readFromNet(io, self);
		new EventLMPlayerClient.DataChanged(p).post();
		FTBLibClient.onGuiClientAction();
		return null;
	}
}