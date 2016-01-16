package latmod.ftbu.net;

import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;
import latmod.lib.ByteCount;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.*;

import javax.xml.ws.handler.MessageContext;

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
		p.readFromNet(io, p.getProfile().getId().equals(FTBLibClient.getUUID()));
		new EventLMPlayerClient.DataChanged(p).post();
		FTBLibClient.onGuiClientAction();
		return null;
	}
}